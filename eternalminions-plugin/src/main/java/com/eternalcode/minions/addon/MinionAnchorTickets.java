package com.eternalcode.minions.addon;

import com.eternalcode.minions.minion.Minion;
import com.eternalcode.minions.minion.MinionId;
import com.eternalcode.minions.minion.MinionPosition;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.key.Key;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public final class MinionAnchorTickets {

    private final Plugin plugin;
    private final MinionFuelService fuels;
    private final Map<MinionId, AnchoredChunk> anchored = new HashMap<>();
    private final Map<AnchoredChunk, Integer> holders = new HashMap<>();

    public MinionAnchorTickets(Plugin plugin, MinionFuelService fuels) {
        this.plugin = plugin;
        this.fuels = fuels;
    }

    public void update(Minion minion) {
        boolean anchors = this.fuels.anchors(minion);
        AnchoredChunk current = this.anchored.get(minion.id());
        if (anchors && current == null) {
            this.acquire(minion);
            return;
        }
        if (!anchors && current != null) {
            this.release(minion.id());
        }
    }

    public void release(MinionId minionId) {
        AnchoredChunk chunk = this.anchored.remove(minionId);
        if (chunk == null) {
            return;
        }

        int remaining = this.holders.merge(chunk, -1, Integer::sum);
        if (remaining > 0) {
            return;
        }

        this.holders.remove(chunk);
        World world = this.plugin.getServer().getWorld(Key.key(chunk.worldKey()));
        if (world != null) {
            world.removePluginChunkTicket(chunk.chunkX(), chunk.chunkZ(), this.plugin);
        }
    }

    public void releaseAll() {
        for (MinionId minionId : Map.copyOf(this.anchored).keySet()) {
            this.release(minionId);
        }
    }

    private void acquire(Minion minion) {
        MinionPosition position = minion.position();
        World world = this.plugin.getServer().getWorld(Key.key(position.worldKey()));
        if (world == null) {
            return;
        }

        AnchoredChunk chunk = new AnchoredChunk(position.worldKey(), position.blockX() >> 4, position.blockZ() >> 4);
        this.anchored.put(minion.id(), chunk);
        if (this.holders.merge(chunk, 1, Integer::sum) == 1) {
            world.addPluginChunkTicket(chunk.chunkX(), chunk.chunkZ(), this.plugin);
        }
    }

    private record AnchoredChunk(String worldKey, int chunkX, int chunkZ) {
    }
}
