package com.eternalcode.minions.addon;

import org.bukkit.Material;

public enum MinionArmorSet {
    NETHERITE(Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
    DIAMOND(Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
    GOLDEN(Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS),
    IRON(Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS),
    CHAINMAIL(Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);

    private final Material chestplate;
    private final Material leggings;
    private final Material boots;

    MinionArmorSet(Material chestplate, Material leggings, Material boots) {
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    public Material chestplate() {
        return this.chestplate;
    }

    public Material leggings() {
        return this.leggings;
    }

    public Material boots() {
        return this.boots;
    }
}
