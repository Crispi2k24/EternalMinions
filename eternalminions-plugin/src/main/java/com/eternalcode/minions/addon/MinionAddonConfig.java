package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.ConfigurationFile;
import eu.okaeri.configs.annotation.Comment;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MinionAddonConfig extends ConfigurationFile {

    @Override
    public Path resolve(Path dataDirectory) {
        return dataDirectory.resolve("addons.yml");
    }

    @Comment({
        "Items accepted by the fuel slot of the minion panel, keyed by id.",
        "Ids must be unique across fuels and modules."
    })
    public Map<String, MinionFuelConfig> fuels = defaultFuels();

    @Comment({
        "Items accepted by the two module slots of the minion panel, keyed by id.",
        "Two modules with the same effect do not stack."
    })
    public Map<String, MinionModuleConfig> modules = defaultModules();

    @Comment("How many items the SUPER_COMPACT module turns into one compressed item.")
    public int compressAmount = 160;

    @Comment("Compressed items made by the SUPER_COMPACT module, keyed by the material they are made of.")
    public Map<XMaterial, MinionAddonItemConfig> compressedItems = defaultCompressedItems();

    public Optional<MinionAddonItemConfig> find(MinionAddon addon) {
        if (addon.type() == MinionAddonType.FUEL) {
            return this.fuel(addon.id()).map(fuel -> fuel.item);
        }
        return this.module(addon.id()).map(module -> module.item);
    }

    public Optional<MinionFuelConfig> fuel(String id) {
        return Optional.ofNullable(this.fuels.get(id));
    }

    public Optional<MinionModuleConfig> module(String id) {
        return Optional.ofNullable(this.modules.get(id));
    }

    public Optional<MinionAddon> find(String id) {
        if (this.fuels.containsKey(id)) {
            return Optional.of(new MinionAddon(MinionAddonType.FUEL, id));
        }
        if (this.modules.containsKey(id)) {
            return Optional.of(new MinionAddon(MinionAddonType.MODULE, id));
        }
        return Optional.empty();
    }

    private static Map<String, MinionFuelConfig> defaultFuels() {
        Map<String, MinionFuelConfig> fuels = new LinkedHashMap<>();
        fuels.put("wegielek", MinionFuelConfig.of(fuel(XMaterial.COAL_BLOCK, false, "<green>Węgielek minionka",
            "<gray>Pracuje szybciej o <aqua>10%<gray>.",
            "<gray>Starcza na <aqua>1 h <gray>pracy."), 10, 60));
        fuels.put("zapalnik", MinionFuelConfig.of(fuel(XMaterial.BLAZE_POWDER, false, "<green>Zapalnik",
            "<gray>Pracuje szybciej o <aqua>25%<gray>.",
            "<gray>Starcza na <aqua>6 h <gray>pracy."), 25, 360));
        fuels.put("serce_magmy", MinionFuelConfig.of(fuel(XMaterial.MAGMA_CREAM, false, "<green>Serce magmy",
            "<gray>Pracuje szybciej o <aqua>50%<gray>.",
            "<gray>Starcza na <aqua>24 h <gray>pracy."), 50, 1440));
        MinionFuelConfig solarPanel = MinionFuelConfig.of(fuel(XMaterial.DAYLIGHT_DETECTOR, false,
            "<green>Panel słoneczny",
            "<gray>W dzień pracuje szybciej o <aqua>15%<gray>.",
            "<gray>Nie zużywa się."), 15, 0);
        solarPanel.daylightOnly = true;
        fuels.put("panel_sloneczny", solarPanel);
        MinionFuelConfig scroll = MinionFuelConfig.of(fuel(XMaterial.PAPER, true, "<green>Zwój nauki",
            "<gray>Poziom minionka rośnie <aqua>2x <gray>szybciej.",
            "<gray>Starcza na <aqua>6 h <gray>pracy."), 0, 360);
        scroll.progressMultiplier = 2.0D;
        fuels.put("zwoj_nauki", scroll);
        fuels.put("katalizator", MinionFuelConfig.of(fuel(XMaterial.END_CRYSTAL, true, "<green>Katalizator",
            "<gray>Pracuje <aqua>2x <gray>szybciej.",
            "<gray>Starcza na <aqua>1 h <gray>pracy."), 100, 60));
        MinionFuelConfig anchor = MinionFuelConfig.of(fuel(XMaterial.RESPAWN_ANCHOR, false, "<green>Kotwica",
            "<gray>Pracuje dalej, gdy jesteś offline",
            "<gray>albo daleko, na <aqua>25% <gray>prędkości.",
            "<gray>Starcza na <aqua>24 h <gray>takiej pracy."), 0, 1440);
        anchor.worksWhileAway = true;
        anchor.awaySpeedPercent = 25;
        fuels.put("kotwica", anchor);
        return fuels;
    }

    private static Map<String, MinionModuleConfig> defaultModules() {
        Map<String, MinionModuleConfig> modules = new LinkedHashMap<>();
        modules.put("piec", MinionModuleConfig.of(module(XMaterial.BLAST_FURNACE, false, "<green>Piec hutniczy",
            "<gray>Przetapia łup jak piec: rudy na",
            "<gray>sztabki, bruk na kamień, drewno na węgiel."), MinionModuleEffect.SMELT));
        modules.put("kompresor", MinionModuleConfig.of(module(XMaterial.PISTON, false, "<green>Kompresor",
            "<gray>Zamienia <aqua>9 <gray>przedmiotów w ich blok,",
            "<gray>np. sztabki żelaza w blok żelaza."), MinionModuleEffect.COMPACT));
        modules.put("zageszczacz", MinionModuleConfig.of(module(XMaterial.STICKY_PISTON, false, "<green>Zagęszczacz",
            "<gray>Zamienia <aqua>160 <gray>surowca w jego",
            "<gray>zagęszczoną wersję do receptur."), MinionModuleEffect.SUPER_COMPACT));
        MinionModuleConfig sieve = MinionModuleConfig.of(module(XMaterial.SCAFFOLDING, false, "<green>Sito",
            "<gray>Przy każdej pracy daje szansę",
            "<gray>na dodatkowy przedmiot."), MinionModuleEffect.BONUS_DROPS);
        sieve.bonusDrops = List.of(
            MinionBonusDropConfig.of(XMaterial.IRON_NUGGET, 1, 5.0D),
            MinionBonusDropConfig.of(XMaterial.GOLD_NUGGET, 1, 2.0D),
            MinionBonusDropConfig.of(XMaterial.DIAMOND, 1, 0.1D)
        );
        modules.put("sito", sieve);
        MinionModuleConfig hopper = MinionModuleConfig.of(module(XMaterial.HOPPER, false, "<green>Lej sprzedażowy",
            "<gray>Gdy skrzynia i magazyn są pełne,",
            "<gray>sprzedaje nadmiar za <aqua>50% <gray>ceny sklepu."), MinionModuleEffect.SELL_OVERFLOW);
        hopper.sellPercent = 50;
        modules.put("lej", hopper);
        MinionModuleConfig goldenHopper = MinionModuleConfig.of(module(XMaterial.HOPPER, true, "<green>Złoty lej",
            "<gray>Gdy skrzynia i magazyn są pełne,",
            "<gray>sprzedaje nadmiar za <aqua>80% <gray>ceny sklepu."), MinionModuleEffect.SELL_OVERFLOW);
        goldenHopper.sellPercent = 80;
        modules.put("zloty_lej", goldenHopper);
        return modules;
    }

    private static Map<XMaterial, MinionAddonItemConfig> defaultCompressedItems() {
        Map<XMaterial, MinionAddonItemConfig> items = new LinkedHashMap<>();
        items.put(XMaterial.COBBLESTONE, compressed(XMaterial.COBBLESTONE, "Zagęszczony bruk"));
        items.put(XMaterial.STONE, compressed(XMaterial.STONE, "Zagęszczony kamień"));
        items.put(XMaterial.GRAVEL, compressed(XMaterial.GRAVEL, "Zagęszczony żwir"));
        items.put(XMaterial.COAL, compressed(XMaterial.COAL, "Zagęszczony węgiel"));
        items.put(XMaterial.IRON_INGOT, compressed(XMaterial.IRON_INGOT, "Zagęszczone żelazo"));
        items.put(XMaterial.GOLD_INGOT, compressed(XMaterial.GOLD_INGOT, "Zagęszczone złoto"));
        items.put(XMaterial.REDSTONE, compressed(XMaterial.REDSTONE, "Zagęszczony redstone"));
        items.put(XMaterial.LAPIS_LAZULI, compressed(XMaterial.LAPIS_LAZULI, "Zagęszczony lapis"));
        items.put(XMaterial.DIAMOND, compressed(XMaterial.DIAMOND, "Zagęszczony diament"));
        items.put(XMaterial.EMERALD, compressed(XMaterial.EMERALD, "Zagęszczony szmaragd"));
        items.put(XMaterial.OBSIDIAN, compressed(XMaterial.OBSIDIAN, "Zagęszczony obsydian"));
        items.put(XMaterial.GLASS, compressed(XMaterial.GLASS, "Zagęszczone szkło"));
        items.put(XMaterial.OAK_LOG, compressed(XMaterial.OAK_LOG, "Zagęszczone drewno"));
        items.put(XMaterial.WHEAT, compressed(XMaterial.WHEAT, "Zagęszczona pszenica"));
        items.put(XMaterial.SUGAR_CANE, compressed(XMaterial.SUGAR_CANE, "Zagęszczona trzcina"));
        items.put(XMaterial.PAPER, compressed(XMaterial.PAPER, "Zagęszczony papier"));
        return items;
    }

    private static MinionAddonItemConfig compressed(XMaterial material, String name) {
        return MinionAddonItemConfig.of(material, true, "<green>" + name,
            "<dark_gray>Surowiec do receptur",
            "",
            "<gray>Powstaje w minionku z modułem",
            "<aqua>Zagęszczacz<gray>.");
    }

    private static MinionAddonItemConfig fuel(XMaterial material, boolean glowing, String name, String... effect) {
        return MinionAddonItemConfig.of(material, glowing, name, lore("Paliwo minionka", "Paliwo", effect));
    }

    private static MinionAddonItemConfig module(XMaterial material, boolean glowing, String name, String... effect) {
        return MinionAddonItemConfig.of(material, glowing, name, lore("Moduł minionka", "Moduł", effect));
    }

    private static String[] lore(String kind, String slot, String... effect) {
        String[] lore = new String[effect.length + 4];
        lore[0] = "<dark_gray>" + kind;
        lore[1] = "";
        System.arraycopy(effect, 0, lore, 2, effect.length);
        lore[effect.length + 2] = "";
        lore[effect.length + 3] = "<yellow>Włóż w pole " + slot + " w oknie minionka!";
        return lore;
    }
}
