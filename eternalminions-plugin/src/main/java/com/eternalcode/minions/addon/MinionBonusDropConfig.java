package com.eternalcode.minions.addon;

import com.cryptomorin.xseries.XMaterial;
import eu.okaeri.configs.OkaeriConfig;

public final class MinionBonusDropConfig extends OkaeriConfig {

    public XMaterial material = XMaterial.IRON_NUGGET;
    public int amount = 1;
    public double chancePercent = 1.0D;

    static MinionBonusDropConfig of(XMaterial material, int amount, double chancePercent) {
        MinionBonusDropConfig drop = new MinionBonusDropConfig();
        drop.material = material;
        drop.amount = amount;
        drop.chancePercent = chancePercent;
        return drop;
    }
}
