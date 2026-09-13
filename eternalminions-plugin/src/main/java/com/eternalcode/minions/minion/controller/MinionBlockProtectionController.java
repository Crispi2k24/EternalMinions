package com.eternalcode.minions.minion.controller;

import com.eternalcode.minions.config.MessagesConfig;
import com.eternalcode.minions.minion.MinionRegistry;
import com.eternalcode.minions.notice.NoticeService;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public final class MinionBlockProtectionController implements Listener {

    private final MinionRegistry minions;
    private final MessagesConfig messages;
    private final NoticeService notices;

    public MinionBlockProtectionController(MinionRegistry minions, MessagesConfig messages, NoticeService notices) {
        this.minions = minions;
        this.messages = messages;
        this.notices = notices;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!this.intersectsMinion(event.getBlockPlaced())) {
            return;
        }

        event.setCancelled(true);
        this.notices.create().viewer(event.getPlayer()).notice(this.messages.blockPlacementBlockedByMinion).send();
    }

    private boolean intersectsMinion(Block block) {
        String worldKey = block.getWorld().getKey().asString();

        return this.minions.hasMinionAt(
                worldKey,
                block.getX(),
                block.getY() + 1,
                block.getZ()
        ) || this.minions.hasMinionAt(
                worldKey,
                block.getX(),
                block.getY(),
                block.getZ()
        ) || this.minions.hasMinionAt(
                worldKey,
                block.getX(),
                block.getY() - 1,
                block.getZ()
        );
    }
}
