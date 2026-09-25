package com.eternalcode.minions.gui;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.ConfigurationFile;
import com.eternalcode.minions.config.MinionPanelAction;
import com.eternalcode.minions.config.MinionPanelElementConfig;
import com.eternalcode.minions.minion.upgrade.DefaultUpgradeKinds;
import com.eternalcode.minions.minion.upgrade.UpgradeKind;
import eu.okaeri.configs.annotation.Comment;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MinionPanelConfig extends ConfigurationFile {

    @Override
    public Path resolve(Path dataDirectory) {
        return dataDirectory.resolve("panel.yml");
    }

    @Comment("Text displayed instead of a value when a minion reached its maximum level.")
    public String maximumValue = "MAKS";

    @Comment("Title supports MiniMessage and minion placeholders.")
    public String title = "Minionek {MINION_BEHAVIOR}";

    @Comment("Each row contains exactly 9 symbols. The number of rows defines the inventory size.")
    public List<String> pattern = List.of(
        "#F##I##MN",
        "SSSSSSSSS",
        "SSSSSSSSS",
        "SSSSSSSSS",
        "#########",
        "#TWRCL#UP"
    );

    @Comment("Inventory slot occupied by the profession usage instructions.")
    public int usageInstructionsSlot = 45;

    @Comment("Status texts used by the {MINION_CHEST} placeholder.")
    public String chestLinkedStatus = "<green>podłączona";
    public String chestNotLinkedStatus = "<gray>brak";

    @Comment("Texts used by the {MINION_DIRECTION} placeholder.")
    public String directionSouth = "południe";
    public String directionWest = "zachód";
    public String directionNorth = "północ";
    public String directionEast = "wschód";

    @Comment({
        "Texts used by the {MINION_FUEL_TIME} placeholder - the working time left in the fuel slot.",
        "Placeholders: {HOURS}, {MINUTES}, {TOTAL_MINUTES}."
    })
    public String fuelNoneText = "brak";
    public String fuelUnlimitedText = "bez limitu";
    public String fuelMinutesFormat = "{TOTAL_MINUTES} min";
    public String fuelHoursFormat = "{HOURS} h {MINUTES} min";

    @Comment("Title of the upgrades panel. Supports MiniMessage and minion placeholders.")
    public String upgradesTitle = "Minionek - ulepszenia";

    @Comment({
        "Icons of the upgrades panel, one per upgrade kind.",
        "Placeholders: {UPGRADE_TIER}, {UPGRADE_MAX_TIER}, {UPGRADE_VALUE},",
        "{UPGRADE_NEXT_VALUE} (MAX when fully upgraded)."
    })
    public Map<UpgradeKind, MinionPanelElementConfig> upgradeElements = defaultUpgradeElements();

    @Comment({
        "Lore appended to an upgrade icon that can still be purchased.",
        "Placeholders: {UPGRADE_REQUIRED_LEVEL}, {UPGRADE_COST}."
    })
    public List<String> upgradeAvailableLore = List.of(
        "<gray>Wymagany poziom: <gold>{UPGRADE_REQUIRED_LEVEL}",
        "<gray>Koszt: <yellow>{UPGRADE_COST}",
        "",
        "<yellow>Kliknij, aby ulepszyć!"
    );

    @Comment({
        "Button in the upgrades panel that returns to the minion panel.",
        "Material AIR hides it."
    })
    public MinionPanelElementConfig upgradesBack = element(
        MinionPanelAction.NONE,
        XMaterial.ARROW,
        "<yellow>Powrót",
        "<gray>Wróć do panelu minionka."
    );

    @Comment("Title of the skin selection panel. Supports MiniMessage.")
    public String skinsTitle = "Minionek - wygląd";

    @Comment("Text used by the {MINION_SKIN} placeholder when the minion wears its profession's own look.")
    public String skinDefaultText = "domyślny";

    @Comment("Icon of the profession's own look in the skin selection panel.")
    public MinionPanelElementConfig skinDefault = element(
        MinionPanelAction.NONE,
        XMaterial.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
        "<green>Domyślny wygląd",
        "<gray>Netherytowa zbroja ze wzorem Strażnik."
    );

    @Comment("Lore appended to a skin the player owns or got for free.")
    public List<String> skinOwnedLore = List.of("", "<yellow>Kliknij, aby założyć!");

    @Comment("Lore appended to the skin the minion wears.")
    public List<String> skinWornLore = List.of("", "<green>Minionek nosi ten wygląd.");

    @Comment("Lore appended to a skin the player can buy. Placeholder: {SKIN_PRICE}.")
    public List<String> skinLockedLore = List.of(
        "",
        "<gray>Cena: <yellow>{SKIN_PRICE}",
        "",
        "<yellow>Kliknij, aby kupić!"
    );

    @Comment("Lore appended to an upgrade icon that reached its maximum tier.")
    public List<String> upgradeMaximumLore = List.of(
        "<green>Ulepszone do maksimum!"
    );

    private static Map<UpgradeKind, MinionPanelElementConfig> defaultUpgradeElements() {
        Map<UpgradeKind, MinionPanelElementConfig> elements = new LinkedHashMap<>();
        elements.put(
                DefaultUpgradeKinds.SPEED, element(
            MinionPanelAction.NONE,
            XMaterial.SUGAR,
            "<green>Szybkość <dark_gray>({UPGRADE_TIER}/{UPGRADE_MAX_TIER})",
            "<gray>Przerwa między kolejnymi",
            "<gray>zadaniami minionka:",
            "<aqua>{UPGRADE_VALUE} <dark_gray>→ <aqua>{UPGRADE_NEXT_VALUE} <dark_gray>(20 = 1 sekunda)",
            ""
        ));
        elements.put(
                DefaultUpgradeKinds.RANGE, element(
            MinionPanelAction.NONE,
            XMaterial.SPYGLASS,
            "<green>Zasięg <dark_gray>({UPGRADE_TIER}/{UPGRADE_MAX_TIER})",
            "<gray>Na ile bloków wokół siebie pracuje:",
            "<aqua>{UPGRADE_VALUE} <dark_gray>→ <aqua>{UPGRADE_NEXT_VALUE}",
            ""
        ));
        elements.put(
                DefaultUpgradeKinds.CAPACITY, element(
            MinionPanelAction.NONE,
            XMaterial.CHEST,
            "<green>Pojemność <dark_gray>({UPGRADE_TIER}/{UPGRADE_MAX_TIER})",
            "<gray>Miejsca w magazynie minionka:",
            "<aqua>{UPGRADE_VALUE} <dark_gray>→ <aqua>{UPGRADE_NEXT_VALUE}",
            ""
        ));
        return elements;
    }

    @Comment({
        "Every symbol used by pattern must have one complete element definition.",
        "An element with action NONE and material AIR leaves its slots empty."
    })
    public Map<Character, MinionPanelElementConfig> elements = defaultElements();

    @Comment({
        "Icon of storage slots above the minion's current capacity.",
        "Material AIR leaves them empty."
    })
    public MinionPanelElementConfig lockedStorageSlot = element(
        MinionPanelAction.NONE,
        XMaterial.GRAY_STAINED_GLASS_PANE,
        "<red>Zablokowane miejsce",
        "<gray>Odblokujesz je ulepszeniem",
        "<aqua>Pojemność<gray>."
    );

    private static Map<Character, MinionPanelElementConfig> defaultElements() {
        Map<Character, MinionPanelElementConfig> elements = new LinkedHashMap<>();
        elements.put('#', element(MinionPanelAction.NONE, XMaterial.AIR, " "));
        elements.put('F', element(
            MinionPanelAction.FUEL_SLOT,
            XMaterial.COAL,
            "<red>Brak paliwa",
            "<gray>Paliwo przyspiesza minionka",
            "<gray>albo daje mu inne bonusy.",
            "",
            "<yellow>Kliknij, trzymając paliwo!"
        ));
        elements.put('M', element(
            MinionPanelAction.FIRST_MODULE_SLOT,
            XMaterial.LIGHT_GRAY_DYE,
            "<red>Brak modułu",
            "<gray>Moduł zmienia to, co minionek",
            "<gray>produkuje, np. przetapia łup.",
            "",
            "<yellow>Kliknij, trzymając moduł!"
        ));
        elements.put('N', element(
            MinionPanelAction.SECOND_MODULE_SLOT,
            XMaterial.LIGHT_GRAY_DYE,
            "<red>Brak modułu",
            "<gray>Moduł zmienia to, co minionek",
            "<gray>produkuje, np. przetapia łup.",
            "",
            "<yellow>Kliknij, trzymając moduł!"
        ));
        elements.put('I', element(
            MinionPanelAction.MINION_INFORMATION,
            XMaterial.NETHER_STAR,
            "<green>Minionek {MINION_BEHAVIOR}",
            "<gray>Poziom: <gold>{MINION_LEVEL}<dark_gray>/<gold>{MINION_MAX_LEVEL}",
            "<gray>Wykonane prace: <aqua>{MINION_PROGRESS}",
            "<gray>Następny poziom przy: <aqua>{MINION_PROGRESS_REQUIRED}",
            "{MINION_PROGRESS_BAR}",
            "",
            "<gray>Magazyn: <aqua>{STORAGE_USED}<dark_gray>/<aqua>{STORAGE_CAPACITY}",
            "<gray>Paliwo: <aqua>{MINION_FUEL_TIME}",
            "<gray>Kierunek: <aqua>{MINION_DIRECTION}",
            "<gray>Skrzynia: {MINION_CHEST}"
        ));
        elements.put('S', element(MinionPanelAction.STORAGE_SLOT, XMaterial.AIR, " "));
        elements.put('T', element(
            MinionPanelAction.TOOL_SLOT,
            XMaterial.GRAY_DYE,
            "<red>Brak narzędzia",
            "<gray>Bez narzędzia minionek",
            "<gray>nie zabierze się do pracy.",
            "",
            "<yellow>Kliknij, trzymając narzędzie!"
        ));
        elements.put('R', element(
            MinionPanelAction.ROTATE,
            XMaterial.COMPASS,
            "<green>Kierunek pracy",
            "<gray>Teraz: <aqua>{MINION_DIRECTION}",
            "",
            "<yellow>Kliknij, aby obrócić!"
        ));
        elements.put('C', element(
            MinionPanelAction.COLLECT_ITEMS,
            XMaterial.CHEST,
            "<green>Zabierz przedmioty",
            "<gray>W magazynie: <aqua>{STORAGE_USED}<dark_gray>/<aqua>{STORAGE_CAPACITY}",
            "",
            "<yellow>Kliknij, aby zabrać wszystko!"
        ));
        elements.put('L', element(
            MinionPanelAction.LINK_CHEST,
            XMaterial.HOPPER,
            "<green>Podłączona skrzynia",
            "<gray>Łup trafia prosto do tej skrzyni,",
            "<gray>a gdy się zapełni, do magazynu.",
            "",
            "<gray>Skrzynia: {MINION_CHEST}",
            "",
            "<yellow>Kliknij, aby podłączyć lub odłączyć!"
        ));
        elements.put('W', element(
            MinionPanelAction.SKINS,
            XMaterial.NETHERITE_CHESTPLATE,
            "<green>Wygląd",
            "<gray>Skórka: <aqua>{MINION_SKIN}",
            "<gray>Kolor zbroi rośnie z poziomem",
            "<gray>i ulepszeniami minionka.",
            "",
            "<gray>Nowe wyglądy kupisz tu za monety wyspy.",
            "",
            "<yellow>Kliknij, aby zmienić!"
        ));
        elements.put('U', element(
            MinionPanelAction.UPGRADES,
            XMaterial.EXPERIENCE_BOTTLE,
            "<green>Ulepszenia",
            "<gray>Szybkość, zasięg i pojemność",
            "<gray>minionka za monety wyspy.",
            "",
            "<yellow>Kliknij, aby zobaczyć!"
        ));
        elements.put('P', element(
            MinionPanelAction.PICKUP_MINION,
            XMaterial.BARRIER,
            "<red>Podnieś minionka",
            "<gray>Wróci do ekwipunku razem",
            "<gray>z poziomem, ulepszeniami i narzędziem.",
            "",
            "<yellow>Kliknij, aby podnieść!"
        ));
        return elements;
    }

    private static MinionPanelElementConfig element(
        MinionPanelAction action,
        XMaterial material,
        String displayName,
        String... lore
    ) {
        MinionPanelElementConfig element = new MinionPanelElementConfig();
        element.action = action;
        element.material = material;
        element.displayName = displayName;
        element.lore = List.of(lore);
        return element;
    }
}
