package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.item.MinionAppearanceItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class MinionAddonItems {

    private static final char SEPARATOR = ':';

    private final MinionAddonConfig config;
    private final MinionAppearanceItems appearance;
    private final MiniMessage miniMessage;
    private final NamespacedKey addonKey;
    private final NamespacedKey compressedKey;

    public MinionAddonItems(
        Plugin plugin,
        MinionAddonConfig config,
        MinionAppearanceItems appearance,
        MiniMessage miniMessage
    ) {
        this.config = config;
        this.appearance = appearance;
        this.miniMessage = miniMessage;
        this.addonKey = new NamespacedKey(plugin, "minion_addon");
        this.compressedKey = new NamespacedKey(plugin, "minion_compressed");
    }

    public Optional<ItemStack> create(String id, int amount) {
        return this.config.find(id).map(addon -> this.create(addon, amount));
    }

    public Optional<ItemStack> createCompressed(XMaterial material, int amount) {
        MinionAddonItemConfig definition = this.config.compressedItems.get(material);
        if (definition == null) {
            return Optional.empty();
        }

        ItemStack item = this.build(definition, amount);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(this.compressedKey, PersistentDataType.STRING, material.name());
        item.setItemMeta(meta);
        return Optional.of(item);
    }

    public Optional<MinionAddon> read(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return Optional.empty();
        }

        String stored = item.getItemMeta().getPersistentDataContainer().get(this.addonKey, PersistentDataType.STRING);
        if (stored == null) {
            return Optional.empty();
        }

        int separator = stored.indexOf(SEPARATOR);
        if (separator < 0) {
            return Optional.empty();
        }

        try {
            MinionAddon addon = new MinionAddon(
                MinionAddonType.valueOf(stored.substring(0, separator)),
                stored.substring(separator + 1)
            );
            return this.config.find(addon).isPresent() ? Optional.of(addon) : Optional.empty();
        }
        catch (IllegalArgumentException invalid) {
            return Optional.empty();
        }
    }

    public boolean is(ItemStack item, MinionAddonType type) {
        return this.read(item).filter(addon -> addon.type() == type).isPresent();
    }

    public List<String> ids() {
        List<String> ids = new ArrayList<>(this.config.fuels.keySet());
        ids.addAll(this.config.modules.keySet());
        return List.copyOf(ids);
    }

    private ItemStack create(MinionAddon addon, int amount) {
        ItemStack item = this.build(this.config.find(addon).orElseThrow(), amount);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
            this.addonKey,
            PersistentDataType.STRING,
            addon.type().name() + SEPARATOR + addon.id()
        );
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack build(MinionAddonItemConfig definition, int amount) {
        ItemStack item = definition.material == XMaterial.PLAYER_HEAD
            ? this.appearance.head(definition.headTexture)
            : definition.material.parseItem();
        if (item == null) {
            throw new IllegalStateException("XMaterial " + definition.material + " is unavailable on this server version");
        }

        item.setAmount(Math.max(1, Math.min(amount, item.getMaxStackSize())));
        ItemMeta meta = item.getItemMeta();
        meta.displayName(this.render(definition.displayName));
        List<Component> lore = new ArrayList<>(definition.lore.size());
        for (String line : definition.lore) {
            lore.add(this.render(line));
        }
        meta.lore(lore);
        meta.setEnchantmentGlintOverride(definition.glowing ? Boolean.TRUE : null);
        if (definition.customModelData > 0) {
            var customModelData = meta.getCustomModelDataComponent();
            customModelData.setFloats(List.of((float) definition.customModelData));
            meta.setCustomModelDataComponent(customModelData);
        }
        item.setItemMeta(meta);
        return item;
    }

    private Component render(String input) {
        return this.miniMessage.deserialize(input).decoration(TextDecoration.ITALIC, false);
    }
}
