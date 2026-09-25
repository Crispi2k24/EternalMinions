package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.MinionItemsConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.math.BigDecimal;
import java.util.List;
import org.bukkit.Color;

public final class MinionSkinConfig extends OkaeriConfig {

    public String displayName = "<green>Skin";
    public List<String> lore = List.of();

    @Comment("Price paid from the minion upgrade economy. 0 makes the skin free.")
    public BigDecimal price = new BigDecimal("50000");

    @Comment("Icon of the skin in the skin selection panel.")
    public XMaterial icon = XMaterial.LEATHER_CHESTPLATE;

    @Comment("Armor worn with this skin. An empty helmet texture keeps the profession's own head.")
    public MinionItemsConfig items = new MinionItemsConfig();

    @Comment("NPC renderer only: player skin texture. Empty keeps the profession's own skin.")
    public String npcSkin = "";

    static MinionSkinConfig of(String displayName, XMaterial icon, Color color, String... lore) {
        MinionSkinConfig skin = new MinionSkinConfig();
        skin.displayName = displayName;
        skin.icon = icon;
        skin.items.setLeatherArmorColor(color);
        skin.lore = List.of(lore);
        return skin;
    }
}
