package com.eternalcode.minions.addon;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

public final class MinionFuelConfig extends OkaeriConfig {

    public MinionAddonItemConfig item = new MinionAddonItemConfig();

    @Comment("How much faster the minion works, in percent. 100 halves the time between actions.")
    public int speedBonusPercent;

    @Comment("Multiplier of the level progress gained per action. 1.0 leaves it unchanged.")
    public double progressMultiplier = 1.0D;

    @Comment({
        "Working time one item lasts, in minutes. 0 means the fuel is never used up.",
        "Time passes only while the minion actually works."
    })
    public int workMinutes = 60;

    @Comment("If true, the speed bonus applies only during the day.")
    public boolean daylightOnly;

    @Comment({
        "If true, the minion keeps working while its owner is offline or away,",
        "and its chunk is kept loaded while this fuel lasts. Time passes only while that happens."
    })
    public boolean worksWhileAway;

    @Comment("Speed of a minion working away from its owner, in percent of the normal speed.")
    public int awaySpeedPercent = 25;

    static MinionFuelConfig of(MinionAddonItemConfig item, int speedBonusPercent, int workMinutes) {
        MinionFuelConfig fuel = new MinionFuelConfig();
        fuel.item = item;
        fuel.speedBonusPercent = speedBonusPercent;
        fuel.workMinutes = workMinutes;
        return fuel;
    }
}
