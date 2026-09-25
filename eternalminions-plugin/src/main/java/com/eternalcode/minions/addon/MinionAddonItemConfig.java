package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.List;

public final class MinionAddonItemConfig extends OkaeriConfig {

    @Comment("Item material. PLAYER_HEAD together with headTexture gives a textured head.")
    public XMaterial material = XMaterial.PLAYER_HEAD;

    @Comment("Base64 head texture, used only when material is PLAYER_HEAD.")
    public String headTexture = "";

    public String displayName = "<green>Addon";
    public List<String> lore = List.of();
    public boolean glowing;
    public int customModelData;

    static MinionAddonItemConfig of(XMaterial material, boolean glowing, String displayName, String... lore) {
        MinionAddonItemConfig item = new MinionAddonItemConfig();
        item.material = material;
        item.glowing = glowing;
        item.displayName = displayName;
        item.lore = List.of(lore);
        return item;
    }
}
