package com.eternalcode.minions.item;

import com.eternalcode.minions.addon.MinionSkins;
import com.eternalcode.minions.config.MinionsConfig;
import com.eternalcode.minions.minion.Minion;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.Map;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

public final class MinionTrimArmor {

    private final MinionsConfig config;
    private final MinionSkins skins;

    public MinionTrimArmor(MinionsConfig config, MinionSkins skins) {
        this.config = config;
        this.skins = skins;
    }

    public boolean enabled() {
        return this.config.armor.netheriteTrims;
    }

    public ItemStack chestplate(Minion minion) {
        return this.piece(Material.NETHERITE_CHESTPLATE, minion);
    }

    public ItemStack leggings(Minion minion) {
        return this.piece(Material.NETHERITE_LEGGINGS, minion);
    }

    public ItemStack boots(Minion minion) {
        return this.piece(Material.NETHERITE_BOOTS, minion);
    }

    public static int progress(Minion minion) {
        int progress = minion.progress().level() - 1;
        for (int tier : minion.upgrades().entries().values()) {
            progress += tier;
        }
        return progress;
    }

    private ItemStack piece(Material material, Minion minion) {
        ItemStack item = new ItemStack(material);
        TrimMaterial trimMaterial = find(RegistryKey.TRIM_MATERIAL, this.materialKey(progress(minion)));
        TrimPattern trimPattern = find(RegistryKey.TRIM_PATTERN, this.patternKey(minion));
        if (trimMaterial != null && trimPattern != null) {
            item.editMeta(ArmorMeta.class, meta -> meta.setTrim(new ArmorTrim(trimMaterial, trimPattern)));
        }
        return item;
    }

    private String materialKey(int progress) {
        String material = null;
        int reached = Integer.MIN_VALUE;
        for (Map.Entry<Integer, String> entry : this.config.armor.progressMaterials.entrySet()) {
            if (entry.getKey() <= progress && entry.getKey() > reached) {
                reached = entry.getKey();
                material = entry.getValue();
            }
        }
        return material;
    }

    private String patternKey(Minion minion) {
        return this.skins.skin(minion)
            .map(skin -> skin.trimPattern)
            .filter(pattern -> !pattern.isBlank())
            .orElse(this.config.armor.defaultPattern);
    }

    private static <T extends Keyed> T find(RegistryKey<T> registry, String key) {
        NamespacedKey namespacedKey = key == null ? null : NamespacedKey.fromString(key);
        return namespacedKey == null ? null : RegistryAccess.registryAccess().getRegistry(registry).get(namespacedKey);
    }
}
