package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.minion.Minion;
import com.eternalcode.minions.minion.storage.MinionStorage;
import com.eternalcode.minions.shop.MinionShopProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public final class MinionModuleService {

    private static final int COMPACT_AMOUNT = 9;
    private static final int STORAGE_STACK_SIZE = 64;

    private final Server server;
    private final MinionAddonConfig config;
    private final MinionAddonItems items;
    private final MinionShopProvider shop;

    private Map<Material, ItemStack> smeltingResults;
    private Map<Material, ItemStack> compactingResults;

    public MinionModuleService(Server server, MinionAddonConfig config, MinionAddonItems items, MinionShopProvider shop) {
        this.server = server;
        this.config = config;
        this.items = items;
        this.shop = shop;
    }

    public boolean sellsOverflow(Minion minion) {
        return this.sellPercent(this.modules(minion)) > 0;
    }

    public List<ItemStack> processIncoming(Minion minion, Collection<ItemStack> loot) {
        List<MinionModuleConfig> modules = this.modules(minion);
        boolean smelt = hasEffect(modules, MinionModuleEffect.SMELT);
        List<ItemStack> processed = new ArrayList<>(loot.size());
        for (ItemStack item : loot) {
            if (item == null || item.getType().isAir() || item.getAmount() <= 0) {
                continue;
            }
            processed.add(smelt ? this.smelt(item) : item);
        }

        for (MinionModuleConfig module : modules) {
            if (module.effect != MinionModuleEffect.BONUS_DROPS) {
                continue;
            }
            for (MinionBonusDropConfig drop : module.bonusDrops) {
                if (ThreadLocalRandom.current().nextDouble(100.0D) >= drop.chancePercent) {
                    continue;
                }
                ItemStack bonus = drop.material.parseItem();
                if (bonus != null) {
                    bonus.setAmount(Math.max(1, drop.amount));
                    processed.add(bonus);
                }
            }
        }
        return processed;
    }

    public MinionStorage compact(Minion minion, MinionStorage storage) {
        List<MinionModuleConfig> modules = this.modules(minion);
        if (!compacts(modules)) {
            return storage;
        }

        ItemStack[] contents = storage.snapshot();
        return this.compactContents(modules, contents, STORAGE_STACK_SIZE) ? MinionStorage.of(contents) : storage;
    }

    public void compact(Minion minion, Inventory inventory) {
        List<MinionModuleConfig> modules = this.modules(minion);
        if (inventory == null || !compacts(modules)) {
            return;
        }

        ItemStack[] contents = inventory.getStorageContents();
        if (this.compactContents(modules, contents, inventory.getMaxStackSize())) {
            inventory.setStorageContents(contents);
        }
    }

    public List<ItemStack> sellOverflow(Minion minion, List<ItemStack> overflow) {
        if (overflow == null || overflow.isEmpty()) {
            return overflow;
        }

        int percent = this.sellPercent(this.modules(minion));
        if (percent <= 0) {
            return overflow;
        }

        double earned = 0.0D;
        List<ItemStack> unsold = new ArrayList<>();
        for (ItemStack item : overflow) {
            double price = item.hasItemMeta() ? 0.0D : this.shop.priceOf(item.getType());
            if (price <= 0.0D) {
                unsold.add(item);
                continue;
            }
            earned += price * item.getAmount() * percent / 100.0D;
        }

        if (earned > 0.0D && !this.shop.tryPayout(minion.ownerId(), earned)) {
            return overflow;
        }
        return unsold;
    }

    private List<MinionModuleConfig> modules(Minion minion) {
        List<MinionModuleConfig> modules = new ArrayList<>(2);
        Set<MinionModuleEffect> effects = EnumSet.noneOf(MinionModuleEffect.class);
        this.addModule(modules, effects, minion.equipment().firstModule());
        this.addModule(modules, effects, minion.equipment().secondModule());
        return modules;
    }

    private void addModule(List<MinionModuleConfig> modules, Set<MinionModuleEffect> effects, ItemStack item) {
        this.items.read(item)
            .filter(addon -> addon.type() == MinionAddonType.MODULE)
            .flatMap(addon -> this.config.module(addon.id()))
            .filter(module -> effects.add(module.effect))
            .ifPresent(modules::add);
    }

    private int sellPercent(List<MinionModuleConfig> modules) {
        int percent = 0;
        for (MinionModuleConfig module : modules) {
            if (module.effect == MinionModuleEffect.SELL_OVERFLOW) {
                percent = Math.max(percent, Math.min(100, module.sellPercent));
            }
        }
        return percent;
    }

    private ItemStack smelt(ItemStack item) {
        if (item.hasItemMeta()) {
            return item;
        }

        ItemStack result = this.smeltingResults().get(item.getType());
        if (result == null) {
            return item;
        }

        ItemStack smelted = result.clone();
        smelted.setAmount(item.getAmount() * result.getAmount());
        return smelted;
    }

    private boolean compactContents(List<MinionModuleConfig> modules, ItemStack[] contents, int maximumStackSize) {
        boolean changed = false;
        if (hasEffect(modules, MinionModuleEffect.SUPER_COMPACT)) {
            Map<Material, Integer> counts = plainCounts(contents);
            for (Map.Entry<XMaterial, MinionAddonItemConfig> entry : this.config.compressedItems.entrySet()) {
                Material material = entry.getKey().get();
                int available = material == null ? 0 : counts.getOrDefault(material, 0);
                if (available < this.config.compressAmount || this.config.compressAmount <= 0) {
                    continue;
                }
                ItemStack compressed = this.items.createCompressed(entry.getKey(), 1).orElse(null);
                if (compressed != null) {
                    changed |= compactMaterial(
                        contents, maximumStackSize, material, compressed, this.config.compressAmount, available);
                }
            }
        }

        if (hasEffect(modules, MinionModuleEffect.COMPACT)) {
            Map<Material, Integer> counts = plainCounts(contents);
            for (Map.Entry<Material, Integer> entry : counts.entrySet()) {
                ItemStack block = this.compactingResults().get(entry.getKey());
                if (block != null && entry.getValue() >= COMPACT_AMOUNT) {
                    changed |= compactMaterial(
                        contents, maximumStackSize, entry.getKey(), block, COMPACT_AMOUNT, entry.getValue());
                }
            }
        }
        return changed;
    }

    private static boolean compactMaterial(
        ItemStack[] contents,
        int maximumStackSize,
        Material material,
        ItemStack result,
        int needed,
        int available
    ) {
        int times = available / needed;
        if (times <= 0) {
            return false;
        }

        remove(contents, material, times * needed);
        int notPlaced = add(contents, maximumStackSize, result, times);
        if (notPlaced > 0) {
            add(contents, maximumStackSize, new ItemStack(material), notPlaced * needed);
        }
        return notPlaced < times;
    }

    private static void remove(ItemStack[] contents, Material material, int amount) {
        int remaining = amount;
        for (int slot = contents.length - 1; slot >= 0 && remaining > 0; slot--) {
            ItemStack item = contents[slot];
            if (!isPlain(item, material)) {
                continue;
            }
            int taken = Math.min(remaining, item.getAmount());
            remaining -= taken;
            if (taken == item.getAmount()) {
                contents[slot] = null;
                continue;
            }
            ItemStack reduced = item.clone();
            reduced.setAmount(item.getAmount() - taken);
            contents[slot] = reduced;
        }
    }

    private static int add(ItemStack[] contents, int maximumStackSize, ItemStack template, int amount) {
        int stackSize = Math.min(template.getMaxStackSize(), maximumStackSize);
        int remaining = amount;
        for (int slot = 0; slot < contents.length && remaining > 0; slot++) {
            ItemStack item = contents[slot];
            if (item == null || !item.isSimilar(template) || item.getAmount() >= stackSize) {
                continue;
            }
            int moved = Math.min(remaining, stackSize - item.getAmount());
            ItemStack grown = item.clone();
            grown.setAmount(item.getAmount() + moved);
            contents[slot] = grown;
            remaining -= moved;
        }
        for (int slot = 0; slot < contents.length && remaining > 0; slot++) {
            if (contents[slot] != null) {
                continue;
            }
            int moved = Math.min(remaining, stackSize);
            ItemStack placed = template.clone();
            placed.setAmount(moved);
            contents[slot] = placed;
            remaining -= moved;
        }
        return remaining;
    }

    private static Map<Material, Integer> plainCounts(ItemStack[] contents) {
        Map<Material, Integer> counts = new EnumMap<>(Material.class);
        for (ItemStack item : contents) {
            if (item != null && !item.getType().isAir() && !item.hasItemMeta()) {
                counts.merge(item.getType(), item.getAmount(), Integer::sum);
            }
        }
        return counts;
    }

    private static boolean isPlain(ItemStack item, Material material) {
        return item != null && item.getType() == material && !item.hasItemMeta();
    }

    private static boolean hasEffect(List<MinionModuleConfig> modules, MinionModuleEffect effect) {
        for (MinionModuleConfig module : modules) {
            if (module.effect == effect) {
                return true;
            }
        }
        return false;
    }

    private static boolean compacts(List<MinionModuleConfig> modules) {
        return hasEffect(modules, MinionModuleEffect.COMPACT) || hasEffect(modules, MinionModuleEffect.SUPER_COMPACT);
    }

    private Map<Material, ItemStack> smeltingResults() {
        if (this.smeltingResults == null) {
            Map<Material, ItemStack> results = new EnumMap<>(Material.class);
            Iterator<Recipe> recipes = this.server.recipeIterator();
            while (recipes.hasNext()) {
                if (recipes.next() instanceof FurnaceRecipe recipe
                    && recipe.getInputChoice() instanceof RecipeChoice.MaterialChoice choice) {
                    for (Material material : choice.getChoices()) {
                        results.putIfAbsent(material, recipe.getResult());
                    }
                }
            }
            this.smeltingResults = Collections.unmodifiableMap(results);
        }
        return this.smeltingResults;
    }

    private Map<Material, ItemStack> compactingResults() {
        if (this.compactingResults == null) {
            Map<Material, ItemStack> results = new EnumMap<>(Material.class);
            Iterator<Recipe> recipes = this.server.recipeIterator();
            while (recipes.hasNext()) {
                if (recipes.next() instanceof ShapedRecipe recipe) {
                    Material material = singleFullGridMaterial(recipe);
                    if (material != null && recipe.getResult().getAmount() == 1) {
                        results.putIfAbsent(material, recipe.getResult());
                    }
                }
            }
            this.compactingResults = Collections.unmodifiableMap(results);
        }
        return this.compactingResults;
    }

    private static Material singleFullGridMaterial(ShapedRecipe recipe) {
        String[] shape = recipe.getShape();
        if (shape.length != 3) {
            return null;
        }

        Set<Character> keys = new HashSet<>();
        for (String row : shape) {
            if (row.length() != 3 || row.indexOf(' ') >= 0) {
                return null;
            }
            for (char key : row.toCharArray()) {
                keys.add(key);
            }
        }
        if (keys.size() != 1) {
            return null;
        }

        RecipeChoice choice = recipe.getChoiceMap().get(keys.iterator().next());
        if (!(choice instanceof RecipeChoice.MaterialChoice materialChoice)
            || materialChoice.getChoices().size() != 1) {
            return null;
        }
        return materialChoice.getChoices().get(0);
    }
}
