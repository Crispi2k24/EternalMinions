package com.eternalcode.minions.minion;

import java.util.Objects;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public final class MinionEquipment {

    private final ItemStack tool;
    private final ItemStack fuel;
    private final int fuelTicksLeft;
    private final ItemStack firstModule;
    private final ItemStack secondModule;
    private final long visualRevision;

    public MinionEquipment(ItemStack tool) {
        this(tool, null, null, null);
    }

    public MinionEquipment(ItemStack tool, ItemStack fuel, ItemStack firstModule, ItemStack secondModule) {
        this(tool, fuel, 0, firstModule, secondModule, 0L);
    }

    private MinionEquipment(
            ItemStack tool,
            ItemStack fuel,
            int fuelTicksLeft,
            ItemStack firstModule,
            ItemStack secondModule,
            long visualRevision
    ) {
        this.tool = copy(tool);
        this.fuel = copy(fuel);
        this.fuelTicksLeft = this.fuel == null ? 0 : Math.max(0, fuelTicksLeft);
        this.firstModule = copy(firstModule);
        this.secondModule = copy(secondModule);
        this.visualRevision = visualRevision;
    }

    public static MinionEquipment empty() {
        return new MinionEquipment(null);
    }

    public ItemStack tool() {
        return copy(this.tool);
    }

    public ItemStack fuel() {
        return copy(this.fuel);
    }

    public int fuelTicksLeft() {
        return this.fuelTicksLeft;
    }

    public ItemStack firstModule() {
        return copy(this.firstModule);
    }

    public ItemStack secondModule() {
        return copy(this.secondModule);
    }

    public MinionEquipment withTool(ItemStack tool) {
        return new MinionEquipment(
                tool, this.fuel, this.fuelTicksLeft, this.firstModule, this.secondModule, this.visualRevision + 1L);
    }

    public MinionEquipment withDurability(ItemStack tool) {
        if (Objects.equals(this.tool, tool)) {
            return this;
        }

        return new MinionEquipment(
                tool, this.fuel, this.fuelTicksLeft, this.firstModule, this.secondModule, this.visualRevision);
    }

    public MinionEquipment withFuel(ItemStack fuel) {
        return new MinionEquipment(
                this.tool, fuel, 0, this.firstModule, this.secondModule, this.visualRevision);
    }

    public MinionEquipment withFuelTicksLeft(int fuelTicksLeft) {
        return new MinionEquipment(
                this.tool, this.fuel, fuelTicksLeft, this.firstModule, this.secondModule, this.visualRevision);
    }

    public MinionEquipment withFirstModule(ItemStack module) {
        return new MinionEquipment(
                this.tool, this.fuel, this.fuelTicksLeft, module, this.secondModule, this.visualRevision);
    }

    public MinionEquipment withSecondModule(ItemStack module) {
        return new MinionEquipment(
                this.tool, this.fuel, this.fuelTicksLeft, this.firstModule, module, this.visualRevision);
    }

    public int toolDamage() {
        if (this.tool == null) {
            return -1;
        }

        ItemMeta itemMeta = this.tool.getItemMeta();
        return itemMeta instanceof Damageable damageable ? damageable.getDamage() : -1;
    }

    public boolean hasVisualChangeSince(MinionEquipment previous) {
        if (previous == null) {
            throw new IllegalArgumentException("Previous equipment is required");
        }

        return this.visualRevision != previous.visualRevision;
    }

    public boolean hasAddonChangeSince(MinionEquipment previous) {
        if (previous == null) {
            throw new IllegalArgumentException("Previous equipment is required");
        }

        return !Objects.equals(this.fuel, previous.fuel)
                || !Objects.equals(this.firstModule, previous.firstModule)
                || !Objects.equals(this.secondModule, previous.secondModule);
    }

    private static ItemStack copy(ItemStack item) {
        return item == null ? null : item.clone();
    }
}
