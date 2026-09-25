package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import com.eternalcode.minions.config.ConfigurationFile;
import eu.okaeri.configs.annotation.Comment;
import java.nio.file.Path;
import java.util.LinkedHashMap;
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

    @Comment("Items accepted by the two module slots of the minion panel, keyed by id.")
    public Map<String, MinionAddonItemConfig> modules = defaultModules();

    public Optional<MinionAddonItemConfig> find(MinionAddon addon) {
        if (addon.type() == MinionAddonType.FUEL) {
            return this.fuel(addon.id()).map(fuel -> fuel.item);
        }
        return Optional.ofNullable(this.modules.get(addon.id()));
    }

    public Optional<MinionFuelConfig> fuel(String id) {
        return Optional.ofNullable(this.fuels.get(id));
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

    private static Map<String, MinionAddonItemConfig> defaultModules() {
        Map<String, MinionAddonItemConfig> modules = new LinkedHashMap<>();
        modules.put("piec", module(XMaterial.BLAST_FURNACE, false, "<green>Piec hutniczy",
            "<gray>Przetapia łup: rudy na sztabki,",
            "<gray>bruk na kamień, drewno na węgiel."));
        modules.put("kompresor", module(XMaterial.PISTON, false, "<green>Kompresor",
            "<gray>Zamienia <aqua>9 <gray>sztabek albo węgla w blok."));
        modules.put("zageszczacz", module(XMaterial.STICKY_PISTON, false, "<green>Zagęszczacz",
            "<gray>Zamienia <aqua>160 <gray>surowca w jego",
            "<gray>zagęszczoną wersję do receptur."));
        modules.put("sito", module(XMaterial.SCAFFOLDING, false, "<green>Sito",
            "<gray>Daje szansę na dodatkowy przedmiot."));
        modules.put("lej", module(XMaterial.HOPPER, false, "<green>Lej sprzedażowy",
            "<gray>Gdy magazyn i skrzynia są pełne,",
            "<gray>sprzedaje nadmiar za <aqua>50% <gray>ceny sklepu."));
        modules.put("zloty_lej", module(XMaterial.HOPPER, true, "<green>Złoty lej",
            "<gray>Gdy magazyn i skrzynia są pełne,",
            "<gray>sprzedaje nadmiar za <aqua>80% <gray>ceny sklepu."));
        return modules;
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
