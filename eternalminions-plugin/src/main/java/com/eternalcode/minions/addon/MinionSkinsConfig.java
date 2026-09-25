package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.ConfigurationFile;
import eu.okaeri.configs.annotation.Comment;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

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
        skins.put("kolczuga", MinionSkinConfig.of("<gray>Kolczuga", XMaterial.CHAINMAIL_CHESTPLATE,
            MinionArmorSet.CHAINMAIL, "minecraft:tide", 25_000,
            "<gray>Kolczuga ze wzorem <aqua>Przypływ<gray>."));
        skins.put("zelazny", MinionSkinConfig.of("<white>Żelazny", XMaterial.IRON_CHESTPLATE,
            MinionArmorSet.IRON, "minecraft:wild", 40_000,
            "<gray>Żelazna zbroja ze wzorem <aqua>Dziki<gray>."));
        skins.put("zloty", MinionSkinConfig.of("<gold>Złoty", XMaterial.GOLDEN_CHESTPLATE,
            MinionArmorSet.GOLDEN, "minecraft:eye", 60_000,
            "<gray>Złota zbroja ze wzorem <aqua>Oko<gray>."));
        skins.put("diamentowy", MinionSkinConfig.of("<aqua>Diamentowy", XMaterial.DIAMOND_CHESTPLATE,
            MinionArmorSet.DIAMOND, "minecraft:vex", 100_000,
            "<gray>Diamentowa zbroja ze wzorem <aqua>Duch<gray>."));
        skins.put("iglica", MinionSkinConfig.of("<light_purple>Iglica", XMaterial.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
            MinionArmorSet.NETHERITE, "minecraft:spire", 100_000,
            "<gray>Netherytowa zbroja ze wzorem <aqua>Iglica<gray>."));
        skins.put("cisza", MinionSkinConfig.of("<dark_gray>Cisza", XMaterial.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
            MinionArmorSet.NETHERITE, "minecraft:silence", 150_000,
            "<gray>Netherytowa zbroja z najrzadszym", "<gray>wzorem <aqua>Cisza<gray>."));
        return skins;
    }
}
