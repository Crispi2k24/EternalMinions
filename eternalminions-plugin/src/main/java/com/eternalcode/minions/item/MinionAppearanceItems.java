package com.eternalcode.minions.item;

import com.cryptomorin.xseries.XMaterial;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.eternalcode.minions.config.AbstractMinionConfig;
import com.eternalcode.minions.config.MinionArmorPieceConfig;
import com.eternalcode.minions.config.MinionItemsConfig;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class MinionAppearanceItems {

    private final Server server;

    public MinionAppearanceItems(Server server) {
        this.server = server;
    }

    public ItemStack head(AbstractMinionConfig config) {
        return this.head(resolveHeadTexture(config));
    }

    public ItemStack head(String texture) {
        ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
        if (texture.isEmpty()) {
            return head;
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        UUID profileId = UUID.nameUUIDFromBytes(texture.getBytes(StandardCharsets.UTF_8));
        PlayerProfile profile = this.server.createProfile(profileId, "minion");
        profile.setProperty(new ProfileProperty("textures", texture));
        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);
        return head;
    }

    public ItemStack helmet(AbstractMinionConfig config) {
        return this.helmet(config, config.items);
    }

    public ItemStack helmet(AbstractMinionConfig config, MinionItemsConfig items) {
        String texture = items.helmet.texture.isEmpty() ? resolveHeadTexture(config) : items.helmet.texture;
        return this.armor(items.helmet, this.head(texture));
    }

    public ItemStack chestplate(AbstractMinionConfig config) {
        return this.chestplate(config.items);
    }

    public ItemStack chestplate(MinionItemsConfig items) {
        return this.armor(items.chestplate, null);
    }

    public ItemStack leggings(AbstractMinionConfig config) {
        return this.leggings(config.items);
    }

    public ItemStack leggings(MinionItemsConfig items) {
        return this.armor(items.leggings, null);
    }

    public ItemStack boots(AbstractMinionConfig config) {
        return this.boots(config.items);
    }

    public ItemStack boots(MinionItemsConfig items) {
        return this.armor(items.boots, null);
    }

    // The ARMOR_STAND renderer only ever shows this head texture (never npcSkin directly), so if an
    // admin configures only npcSkin - the field meant for the NPC renderer - it still shows up here
    // instead of silently falling back to a blank Steve head.
    private static String resolveHeadTexture(AbstractMinionConfig config) {
        String helmetTexture = config.items.helmet.texture;
        return helmetTexture.isEmpty() ? config.npcSkin : helmetTexture;
    }

    private ItemStack armor(MinionArmorPieceConfig piece, ItemStack head) {
        XMaterial material = piece.type;
        if (material == null || material == XMaterial.AIR) {
            return null;
        }
        if (material == XMaterial.PLAYER_HEAD && head != null) {
            return head;
        }

        ItemStack item = material.parseItem();
        if (item == null) {
            throw new IllegalArgumentException("Material is unavailable: " + material);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta leatherMeta) {
            leatherMeta.setColor(piece.color);
        }
        meta.setEnchantmentGlintOverride(piece.glow);
        item.setItemMeta(meta);
        return item;
    }
}
