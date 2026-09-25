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
        skins.put("duch", MinionSkinConfig.of("<aqua>Duch", XMaterial.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
            "minecraft:vex", 50_000, "<gray>Wzór widma na zbroi minionka."));
        skins.put("dziki", MinionSkinConfig.of("<green>Dziki", XMaterial.WILD_ARMOR_TRIM_SMITHING_TEMPLATE,
            "minecraft:wild", 50_000, "<gray>Wzór prosto z dżungli."));
        skins.put("oko", MinionSkinConfig.of("<dark_aqua>Oko", XMaterial.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
            "minecraft:eye", 75_000, "<gray>Wzór z twierdzy Endu."));
        skins.put("przyplyw", MinionSkinConfig.of("<blue>Przypływ", XMaterial.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
            "minecraft:tide", 75_000, "<gray>Wzór strażnika oceanu."));
        skins.put("iglica", MinionSkinConfig.of("<light_purple>Iglica", XMaterial.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
            "minecraft:spire", 100_000, "<gray>Wzór z miasta Endu."));
        skins.put("cisza", MinionSkinConfig.of("<dark_gray>Cisza", XMaterial.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
            "minecraft:silence", 150_000, "<gray>Najrzadszy wzór, prosto", "<gray>z Pradawnego Miasta."));
        return skins;
    }
}
