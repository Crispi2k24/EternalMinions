package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.MinionItemsConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.math.BigDecimal;
import java.util.List;

public final class MinionSkinConfig extends OkaeriConfig {

    public String displayName = "<green>Skin";
    public List<String> lore = List.of();

    @Comment("Price paid from the minion upgrade economy. 0 makes the skin free.")
    public BigDecimal price = new BigDecimal("50000");

    @Comment("Icon of the skin in the skin selection panel.")
    public XMaterial icon = XMaterial.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE;

    @Comment({
        "Armor trim pattern of this skin, e.g. minecraft:vex. The trim color still follows",
        "the minion's progress. Used while armor.netheriteTrims is enabled in config.yml."
    })
    public String trimPattern = "";

    @Comment({
        "Armor worn with this skin while armor.netheriteTrims is disabled.",
        "An empty helmet texture keeps the profession's own head in both modes."
    })
    public MinionItemsConfig items = new MinionItemsConfig();

    @Comment("NPC renderer only: player skin texture. Empty keeps the profession's own skin.")
    public String npcSkin = "";

    static MinionSkinConfig of(String displayName, XMaterial icon, String trimPattern, int price, String... lore) {
        MinionSkinConfig skin = new MinionSkinConfig();
        skin.displayName = displayName;
        skin.icon = icon;
        skin.trimPattern = trimPattern;
        skin.price = BigDecimal.valueOf(price);
        skin.lore = List.of(lore);
        return skin;
    }
}
