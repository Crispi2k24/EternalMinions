package com.eternalcode.minions.event;

import com.eternalcode.minions.minion.MinionDetails;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class MinionBlockBreakEvent extends MinionEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final MinionDetails minion;
    private final Block block;
    private boolean cancelled;

    public MinionBlockBreakEvent(@NotNull MinionDetails minion, @NotNull Block block) {
        super(MinionEventCause.INTERNAL, null);
        if (minion == null || block == null) {
            throw new IllegalArgumentException("Minion and block are required");
        }
        this.minion = minion;
        this.block = block;
    }

    public @NotNull MinionDetails minion() {
        return this.minion;
    }

    public @NotNull Block block() {
        return this.block;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }
}
