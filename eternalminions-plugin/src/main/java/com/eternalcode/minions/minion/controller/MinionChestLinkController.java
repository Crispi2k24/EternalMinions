package com.eternalcode.minions.minion.controller;

import com.eternalcode.minions.config.MessagesConfig;
import com.eternalcode.minions.minion.Minion;
import com.eternalcode.minions.minion.MinionLifecycleService;
import com.eternalcode.minions.minion.MinionRegistry;
import com.eternalcode.minions.notice.NoticeService;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public final class MinionChestLinkController implements Listener {

    private final MinionRegistry minions;
    private final MinionLifecycleService lifecycle;
    private final MessagesConfig messages;
    private final NoticeService notices;

    public MinionChestLinkController(
            MinionRegistry minions,
            MinionLifecycleService lifecycle,
            MessagesConfig messages,
            NoticeService notices
    ) {
        this.minions = minions;
        this.lifecycle = lifecycle;
        this.messages = messages;
        this.notices = notices;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        this.unlink(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        this.unlink(event.getBlock());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        for (Block block : event.blockList()) {
            this.unlink(block);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            this.unlink(block);
        }
    }

    private void unlink(Block block) {
        if (!(block.getState(false) instanceof Container)) {
            return;
        }

        List<Minion> linked = this.minions.findLinkedToChest(
                block.getWorld().getKey().asString(),
                block.getX(),
                block.getY(),
                block.getZ()
        );

        for (Minion minion : linked) {
            this.lifecycle.updateChestLink(minion.withChestPosition(null));
            this.notifyOwner(minion);
        }
    }

    private void notifyOwner(Minion minion) {
        Player owner = Bukkit.getPlayer(minion.ownerId());

        if (owner == null) {
            return;
        }

        this.notices.create()
                .viewer(owner)
                .notice(this.messages.chestLinkDestroyed)
                .send();
    }
}
