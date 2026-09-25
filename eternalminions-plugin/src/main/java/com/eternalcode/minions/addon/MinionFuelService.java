package com.eternalcode.minions.addon;

import com.eternalcode.minions.config.AbstractMinionConfig;
import com.eternalcode.minions.minion.Minion;
import com.eternalcode.minions.minion.MinionEquipment;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public final class MinionFuelService {

    public static final int TICKS_PER_MINUTE = 1_200;

    private final MinionAddonConfig config;
    private final MinionAddonItems items;

    public MinionFuelService(MinionAddonConfig config, MinionAddonItems items) {
        this.config = config;
        this.items = items;
    }

    public Optional<MinionFuelConfig> activeFuel(Minion minion) {
        return this.items.read(minion.equipment().fuel())
            .filter(addon -> addon.type() == MinionAddonType.FUEL)
            .flatMap(addon -> this.config.fuel(addon.id()));
    }

    public boolean anchors(Minion minion) {
        return this.activeFuel(minion).map(fuel -> fuel.worksWhileAway).orElse(false);
    }

    public double awaySpeed(Minion minion) {
        int percent = this.activeFuel(minion).map(fuel -> fuel.awaySpeedPercent).orElse(100);
        return Math.max(1, Math.min(100, percent)) / 100.0D;
    }

    public long accelerate(Minion minion, World world, long delayTicks, boolean away) {
        MinionFuelConfig fuel = this.activeFuel(minion).orElse(null);
        if (away || fuel == null || fuel.speedBonusPercent <= 0 || fuel.daylightOnly && !world.isDayTime()) {
            return delayTicks;
        }
        return Math.max(1L, Math.round(delayTicks / (1.0D + fuel.speedBonusPercent / 100.0D)));
    }

    public Minion afterWork(
        Minion before,
        Minion after,
        AbstractMinionConfig behaviorConfig,
        long workedTicks,
        boolean away
    ) {
        MinionFuelConfig fuel = this.activeFuel(after).orElse(null);
        if (fuel == null || fuel.worksWhileAway != away) {
            return after;
        }

        Minion updated = after;
        long gained = after.progress().progress() - before.progress().progress();
        if (gained > 0L && fuel.progressMultiplier > 1.0D) {
            updated = updated.withProgress(updated.progress().advancedBy(
                behaviorConfig,
                bonusProgress(gained, fuel.progressMultiplier)
            ));
        }

        if (fuel.workMinutes <= 0) {
            return updated;
        }
        return updated.withEquipment(burn(updated.equipment(), fuel.workMinutes * TICKS_PER_MINUTE, workedTicks));
    }

    public long remainingTicks(Minion minion) {
        MinionFuelConfig fuel = this.activeFuel(minion).orElse(null);
        if (fuel == null) {
            return 0L;
        }
        if (fuel.workMinutes <= 0) {
            return Long.MAX_VALUE;
        }

        MinionEquipment equipment = minion.equipment();
        long unitTicks = (long) fuel.workMinutes * TICKS_PER_MINUTE;
        long current = equipment.fuelTicksLeft() > 0 ? equipment.fuelTicksLeft() : unitTicks;
        return current + (equipment.fuel().getAmount() - 1L) * unitTicks;
    }

    public ItemStack withoutBurningUnit(MinionEquipment equipment) {
        ItemStack fuel = equipment.fuel();
        if (fuel == null || equipment.fuelTicksLeft() <= 0) {
            return fuel;
        }
        if (fuel.getAmount() <= 1) {
            return null;
        }
        fuel.setAmount(fuel.getAmount() - 1);
        return fuel;
    }

    private static MinionEquipment burn(MinionEquipment equipment, int unitTicks, long workedTicks) {
        long ticksLeft = (equipment.fuelTicksLeft() > 0 ? equipment.fuelTicksLeft() : unitTicks) - workedTicks;
        if (ticksLeft > 0L) {
            return equipment.withFuelTicksLeft((int) ticksLeft);
        }

        ItemStack fuel = equipment.fuel();
        if (fuel.getAmount() <= 1) {
            return equipment.withFuel(null);
        }
        fuel.setAmount(fuel.getAmount() - 1);
        return equipment.withFuel(fuel);
    }

    private static long bonusProgress(long gained, double multiplier) {
        double bonus = gained * (multiplier - 1.0D);
        long whole = (long) bonus;
        return ThreadLocalRandom.current().nextDouble() < bonus - whole ? whole + 1L : whole;
    }
}
