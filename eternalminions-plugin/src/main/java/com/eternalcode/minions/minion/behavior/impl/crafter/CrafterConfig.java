package com.eternalcode.minions.minion.behavior.impl.crafter;

import com.eternalcode.minions.config.AbstractMinionConfig;
import com.eternalcode.minions.minion.status.MinionStatus;
import com.eternalcode.minions.minion.tool.ToolCategory;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Include;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;

@Include(AbstractMinionConfig.class)
public final class CrafterConfig extends AbstractMinionConfig {

    @Override
    public Path resolve(Path dataDirectory) {
        return dataDirectory.resolve("minions").resolve("crafter.yml");
    }

    @Comment("Maximum recipes crafted per cycle. Zero crafts until blocked by ingredients, space or safety cap.")
    public int maxCraftsPerCycle = 1;

    @Comment("Hard maximum crafts in one cycle when maxCraftsPerCycle is zero.")
    public int maximumCraftsSafetyCap = 64;

    public CrafterConfig() {
        this.displayName = "<color:#FFB900:#FFD158:#FFB900>ʀᴢᴇᴍɪᴇꜱʟɴɪᴋ";

        this.tool.category = ToolCategory.ANY;
        this.tool.required = false;

        this.items.helmet.texture =
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmNkYzBmZWI3MDAxZTJjMTBmZDUwNjZlNTAxYjg3ZTNkNjQ3OTMwOTJiODVhNTBjODU2ZDk2MmY4YmU5MmM3OCJ9fX0=";

        this.npcSkin =
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vcy5uYW1lbWMuY29tL2kvMjEwNWZkZDUxMzNkZjI3Ni5wbmcifX19";

        this.items.setLeatherArmorColor(
                Color.fromRGB(255, 185, 0)
        );

        this.statuses = defaultStatuses();
        this.usageInstructions.displayName = "<green>Jak używać";
        this.usageInstructions.lore = List.of(
            "<gray>Wytwarza wybrany przedmiot ze",
            "<gray>składników w podłączonej skrzyni.",
            "",
            "<gray>• W pole narzędzia włóż przedmiot,",
            "<gray>  który ma wytwarzać.",
            "<gray>• Podłącz skrzynię i włóż do niej składniki.",
            "",
            "<dark_gray>Gotowe przedmioty trafiają do magazynu."
        );
    }

    private static Map<MinionStatus, String> defaultStatuses() {
        Map<MinionStatus, String> statuses =
                new LinkedHashMap<>();

        statuses.put(
                CrafterStatuses.CRAFTING,
                "<green>Wytwarza przedmioty..."
        );

        statuses.put(
                CrafterStatuses.NO_RECIPE_SELECTED,
                "<red>Nie wybrano, co wytwarzać"
        );

        statuses.put(
                CrafterStatuses.NO_CHEST,
                "<red>Brakuje podłączonej skrzyni"
        );

        statuses.put(
                CrafterStatuses.NO_INGREDIENTS,
                "<gray>Czeka na składniki"
        );

        return statuses;
    }
}
