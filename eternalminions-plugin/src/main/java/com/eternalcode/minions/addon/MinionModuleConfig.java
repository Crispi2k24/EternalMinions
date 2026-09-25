package com.eternalcode.minions.addon;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import java.util.List;

public final class MinionModuleConfig extends OkaeriConfig {

    public MinionAddonItemConfig item = new MinionAddonItemConfig();

    @Comment({
        "What the module does with the minion's loot:",
        "SMELT - smelts loot like a furnace, COMPACT - turns 9 items into their block,",
        "SUPER_COMPACT - turns compressAmount items into their compressed item,",
        "BONUS_DROPS - rolls bonusDrops on every action,",
        "SELL_OVERFLOW - sells loot that fits neither the storage nor the chest for sellPercent of the shop price."
    })
    public MinionModuleEffect effect = MinionModuleEffect.SMELT;

    @Comment("Percent of the shop price paid by SELL_OVERFLOW.")
    public int sellPercent = 50;

    @Comment("Items rolled by BONUS_DROPS on every action.")
    public List<MinionBonusDropConfig> bonusDrops = List.of();

    static MinionModuleConfig of(MinionAddonItemConfig item, MinionModuleEffect effect) {
        MinionModuleConfig module = new MinionModuleConfig();
        module.item = item;
        module.effect = effect;
        return module;
    }
}
