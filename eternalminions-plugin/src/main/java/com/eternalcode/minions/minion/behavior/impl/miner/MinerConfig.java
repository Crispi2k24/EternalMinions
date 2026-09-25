package com.eternalcode.minions.minion.behavior.impl.miner;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.AbstractMinionConfig;
import com.eternalcode.minions.minion.status.MinionStatus;
import com.eternalcode.minions.minion.upgrade.MinionUpgrades;
import com.eternalcode.minions.minion.upgrade.DefaultUpgradeKinds;
import com.eternalcode.minions.minion.tool.ToolCategory;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Include;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import org.bukkit.Color;

@Include(AbstractMinionConfig.class)
public final class MinerConfig extends AbstractMinionConfig {

    @Override
    public Path resolve(Path dataDirectory) {
        return dataDirectory.resolve("minions").resolve("miner.yml");
    }

    @Comment("SQUARE mines the area below the minion. LINE mines in its facing direction.")
    public WorkMode workMode = WorkMode.LINE;

    @Comment("Maximum blocks mined per cycle. Zero mines every matching block found in the work area.")
    public int maxBlocksPerCycle = 1;

    public MinerConfig() {
        this.displayName = "<color:#B7B7B7:#D9D9D9:#B7B7B7>ɢᴏʀɴɪᴋ";
        this.allowedMaterials = List.of(
            XMaterial.COBBLESTONE,
            XMaterial.STONE,
            XMaterial.GRANITE,
            XMaterial.DIORITE,
            XMaterial.ANDESITE,
            XMaterial.TUFF,
            XMaterial.CALCITE,
            XMaterial.DRIPSTONE_BLOCK,
            XMaterial.DEEPSLATE,
            XMaterial.COBBLED_DEEPSLATE,
            XMaterial.NETHERRACK,
            XMaterial.BASALT,
            XMaterial.SMOOTH_BASALT,
            XMaterial.BLACKSTONE,
            XMaterial.END_STONE,
            XMaterial.CINNABAR,
            XMaterial.SULFUR,
            XMaterial.POTENT_SULFUR,
            XMaterial.COAL_ORE,
            XMaterial.DEEPSLATE_COAL_ORE,
            XMaterial.IRON_ORE,
            XMaterial.DEEPSLATE_IRON_ORE,
            XMaterial.COPPER_ORE,
            XMaterial.DEEPSLATE_COPPER_ORE,
            XMaterial.GOLD_ORE,
            XMaterial.DEEPSLATE_GOLD_ORE,
            XMaterial.NETHER_GOLD_ORE,
            XMaterial.REDSTONE_ORE,
            XMaterial.DEEPSLATE_REDSTONE_ORE,
            XMaterial.LAPIS_ORE,
            XMaterial.DEEPSLATE_LAPIS_ORE,
            XMaterial.DIAMOND_ORE,
            XMaterial.DEEPSLATE_DIAMOND_ORE,
            XMaterial.EMERALD_ORE,
            XMaterial.DEEPSLATE_EMERALD_ORE,
            XMaterial.NETHER_QUARTZ_ORE,
            XMaterial.ANCIENT_DEBRIS
        );
        this.tool.category = ToolCategory.PICKAXE;
        this.tool.required = true;
        this.items.helmet.texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk2MjdiZTYyY2VkNzE0MTEzOWQzZjE1NTc5MGE1ZDQzNTZlYjdiOWVlOTVlNTA0YjMzMjI5NzRjYmM1MTVlYSJ9fX0=";
        this.npcSkin = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vcy5uYW1lbWMuY29tL2kvOGM3NmQ3N2Y2NTAzMDAwMy5wbmcifX19";
        this.items.setLeatherArmorColor(Color.fromRGB(183, 183, 183));
        this.statuses = defaultStatuses();
        this.usageInstructions.displayName = "<green>Jak używać";
        this.usageInstructions.lore = List.of(
            "<gray>Kopie bloki w linii przed sobą,",
            "<gray>na tej samej wysokości, na której stoi.",
            "",
            "<gray>• Włóż <aqua>kilof <gray>w pole narzędzia.",
            "<gray>• Obróć go przyciskiem <aqua>Kierunek pracy<gray>.",
            "<gray>• Generator postaw w tej linii.",
            "<gray>• Linia wydłuża się z ulepszeniem <aqua>Zasięg<gray>.",
            "",
            "<dark_gray>Łup trafia do podłączonej skrzyni,",
            "<dark_gray>a gdy jej nie ma albo jest pełna, do magazynu."
        );
    }

    private static Map<MinionStatus, String> defaultStatuses() {
        Map<MinionStatus, String> statuses = new LinkedHashMap<>();
        statuses.put(MinerStatuses.MINING, "<green>Kopie...");
        statuses.put(MinerStatuses.TOOL_TOO_WEAK, "<red>Kilof jest za słaby");
        statuses.put(MinerStatuses.NO_BLOCKS_IN_RANGE, "<gray>Brak bloków w zasięgu");
        statuses.put(MinerStatuses.NO_PICKAXE, "<red>Brakuje kilofa");
        return statuses;
    }

    public int radius(MinionUpgrades upgrades) {
        return this.upgradeTierValue(upgrades, DefaultUpgradeKinds.RANGE, 1);
    }

    public enum WorkMode {

        SQUARE,
        LINE
    }
}
