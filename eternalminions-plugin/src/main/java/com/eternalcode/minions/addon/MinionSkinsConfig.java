package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.ConfigurationFile;
import eu.okaeri.configs.annotation.Comment;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.Color;

public final class MinionSkinsConfig extends ConfigurationFile {

    @Override
    public Path resolve(Path dataDirectory) {
        return dataDirectory.resolve("skins.yml");
    }

    @Comment({
        "Permission that unlocks a skin is this prefix followed by the skin id,",
        "e.g. eternalminions.skin.zloty - it gives the skin for free, without buying it."
    })
    public String permissionPrefix = "eternalminions.skin.";

    @Comment("Skins that can be put on a minion from its panel, keyed by id.")
    public Map<String, MinionSkinConfig> skins = defaultSkins();

    private static Map<String, MinionSkinConfig> defaultSkins() {
        Map<String, MinionSkinConfig> skins = new LinkedHashMap<>();
        skins.put("zloty", MinionSkinConfig.of("<gold>Złoty", XMaterial.GOLD_INGOT, Color.fromRGB(0xF2C94C),
            "<gray>Złota zbroja dla minionka,", "<gray>który zarabia najwięcej."));
        skins.put("lodowy", MinionSkinConfig.of("<aqua>Lodowy", XMaterial.PACKED_ICE, Color.fromRGB(0x8FD3FF),
            "<gray>Chłodny błękit na każdą wyspę."));
        skins.put("ognisty", MinionSkinConfig.of("<red>Ognisty", XMaterial.BLAZE_POWDER, Color.fromRGB(0xE8452C),
            "<gray>Płomienna czerwień dla", "<gray>najszybszych minionków."));
        skins.put("nocny", MinionSkinConfig.of("<dark_gray>Nocny", XMaterial.BLACK_DYE, Color.fromRGB(0x23232B),
            "<gray>Ciemny strój na nocną zmianę."));
        skins.put("lesny", MinionSkinConfig.of("<green>Leśny", XMaterial.OAK_SAPLING, Color.fromRGB(0x3E8E41),
            "<gray>Zieleń prosto z lasu."));
        skins.put("krolewski", MinionSkinConfig.of("<light_purple>Królewski", XMaterial.AMETHYST_SHARD,
            Color.fromRGB(0x8E44AD), "<gray>Purpura godna króla wyspy."));
        return skins;
    }
}
