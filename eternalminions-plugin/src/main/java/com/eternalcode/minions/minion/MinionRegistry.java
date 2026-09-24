package com.eternalcode.minions.minion;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.LongConsumer;

public final class MinionRegistry {

    private final Long2ObjectOpenHashMap<Minion> minions = new Long2ObjectOpenHashMap<>();
    private final Collection<Minion> minionView = Collections.unmodifiableCollection(this.minions.values());
    private final Map<String, Long2ObjectOpenHashMap<LongOpenHashSet>> minionsByWorldChunk = new HashMap<>();
    private final Map<String, Long2ObjectOpenHashMap<LongOpenHashSet>> chestLinksByWorldChunk = new HashMap<>();
    private long lastIssuedId = System.currentTimeMillis();

    public MinionId createId() {
        if (this.lastIssuedId == Long.MAX_VALUE) {
            throw new IllegalStateException("Minion id range is exhausted");
        }

        return new MinionId(++this.lastIssuedId);
    }

    public void register(Minion minion) {
        long id = minion.id().value();
        if (this.minions.putIfAbsent(id, minion) != null) {
            throw new IllegalArgumentException("Minion " + id + " is already registered");
        }
        this.lastIssuedId = Math.max(this.lastIssuedId, id);
        this.index(minion);
        this.indexChestLink(minion);
    }

    public Minion replace(Minion minion) {
        long id = minion.id().value();
        Minion previous = this.minions.replace(id, minion);
        if (previous == null) {
            throw new IllegalArgumentException("Minion " + id + " is not registered");
        }
        if (!previous.position().equals(minion.position())) {
            this.unindex(previous);
            this.index(minion);
        }
        if (!Objects.equals(previous.chestPosition(), minion.chestPosition())) {
            this.unindexChestLink(previous);
            this.indexChestLink(minion);
        }
        return previous;
    }

    public Optional<Minion> remove(MinionId minionId) {
        Minion removed = this.minions.remove(minionId.value());
        if (removed != null) {
            this.unindex(removed);
            this.unindexChestLink(removed);
        }
        return Optional.ofNullable(removed);
    }

    public Optional<Minion> findMinion(MinionId minionId) {
        return Optional.ofNullable(this.minions.get(minionId.value()));
    }

    public Collection<Minion> minions() {
        return this.minionView;
    }

    public boolean hasMinionAt(
            String worldKey,
            int blockX,
            int blockY,
            int blockZ
    ) {
        return this.findAt(worldKey, blockX, blockY, blockZ) != null;
    }

    public Optional<Minion> findAt(MinionPosition position) {
        return Optional.ofNullable(this.findAt(
            position.worldKey(),
            position.blockX(),
            position.blockY(),
            position.blockZ()
        ));
    }

    private Minion findAt(String worldKey, int blockX, int blockY, int blockZ) {
        Long2ObjectOpenHashMap<LongOpenHashSet> worldChunks =
                this.minionsByWorldChunk.get(worldKey);

        if (worldChunks == null) {
            return null;
        }

        LongOpenHashSet ids = worldChunks.get(
                chunkKey(blockX >> 4, blockZ >> 4)
        );

        if (ids == null) {
            return null;
        }

        LongIterator iterator = ids.iterator();

        while (iterator.hasNext()) {
            Minion minion = this.minions.get(iterator.nextLong());

            if (minion == null) {
                continue;
            }

            MinionPosition position = minion.position();

            if (position.blockX() == blockX
                    && position.blockY() == blockY
                    && position.blockZ() == blockZ) {
                return minion;
            }
        }

        return null;
    }

    public List<Minion> findWithin(MinionPosition from, MinionPosition to) {
        if (!from.worldKey().equals(to.worldKey())) {
            throw new IllegalArgumentException("Area corners must be in the same world");
        }

        Long2ObjectOpenHashMap<LongOpenHashSet> worldChunks = this.minionsByWorldChunk.get(from.worldKey());

        if (worldChunks == null) {
            return List.of();
        }

        int minX = Math.min(from.blockX(), to.blockX());
        int minY = Math.min(from.blockY(), to.blockY());
        int minZ = Math.min(from.blockZ(), to.blockZ());
        int maxX = Math.max(from.blockX(), to.blockX());
        int maxY = Math.max(from.blockY(), to.blockY());
        int maxZ = Math.max(from.blockZ(), to.blockZ());
        long areaChunks = ((long) (maxX >> 4) - (minX >> 4) + 1) * ((long) (maxZ >> 4) - (minZ >> 4) + 1);
        List<Minion> found = new ArrayList<>();

        if (areaChunks > worldChunks.size()) {
            for (LongOpenHashSet ids : worldChunks.values()) {
                this.collectWithin(ids, minX, minY, minZ, maxX, maxY, maxZ, found);
            }
            return found;
        }

        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                LongOpenHashSet ids = worldChunks.get(chunkKey(chunkX, chunkZ));

                if (ids != null) {
                    this.collectWithin(ids, minX, minY, minZ, maxX, maxY, maxZ, found);
                }
            }
        }

        return found;
    }

    private void collectWithin(
            LongOpenHashSet ids,
            int minX,
            int minY,
            int minZ,
            int maxX,
            int maxY,
            int maxZ,
            List<Minion> found
    ) {
        LongIterator iterator = ids.iterator();

        while (iterator.hasNext()) {
            Minion minion = this.minions.get(iterator.nextLong());

            if (minion == null) {
                continue;
            }

            MinionPosition position = minion.position();

            if (position.blockX() >= minX && position.blockX() <= maxX
                    && position.blockY() >= minY && position.blockY() <= maxY
                    && position.blockZ() >= minZ && position.blockZ() <= maxZ) {
                found.add(minion);
            }
        }
    }

    public void forEachMinionIdInChunk(String worldKey, int chunkX, int chunkZ, LongConsumer action) {
        Long2ObjectOpenHashMap<LongOpenHashSet> worldChunks = this.minionsByWorldChunk.get(worldKey);
        if (worldChunks == null) {
            return;
        }
        LongOpenHashSet ids = worldChunks.get(chunkKey(chunkX, chunkZ));
        if (ids != null) {
            ids.forEach(action);
        }
    }

    public int countByOwner(UUID ownerId) {
        int count = 0;
        for (Minion minion : this.minions.values()) {
            if (minion.ownerId().equals(ownerId)) {
                count++;
            }
        }
        return count;
    }

    public List<Minion> findLinkedToChest(String worldKey, int blockX, int blockY, int blockZ) {
        Long2ObjectOpenHashMap<LongOpenHashSet> worldChunks = this.chestLinksByWorldChunk.get(worldKey);

        if (worldChunks == null) {
            return List.of();
        }

        LongOpenHashSet ids = worldChunks.get(chunkKey(blockX >> 4, blockZ >> 4));

        if (ids == null) {
            return List.of();
        }

        List<Minion> linked = new ArrayList<>();
        LongIterator iterator = ids.iterator();

        while (iterator.hasNext()) {
            Minion minion = this.minions.get(iterator.nextLong());

            if (minion == null) {
                continue;
            }

            MinionPosition chestPosition = minion.chestPosition();

            if (chestPosition != null
                    && chestPosition.blockX() == blockX
                    && chestPosition.blockY() == blockY
                    && chestPosition.blockZ() == blockZ) {
                linked.add(minion);
            }
        }

        return linked;
    }

    private void indexChestLink(Minion minion) {
        MinionPosition chestPosition = minion.chestPosition();
        if (chestPosition == null) {
            return;
        }
        long chunkKey = chunkKey(chestPosition.blockX() >> 4, chestPosition.blockZ() >> 4);
        this.chestLinksByWorldChunk
            .computeIfAbsent(chestPosition.worldKey(), ignored -> new Long2ObjectOpenHashMap<>())
            .computeIfAbsent(chunkKey, ignored -> new LongOpenHashSet())
            .add(minion.id().value());
    }

    private void unindexChestLink(Minion minion) {
        MinionPosition chestPosition = minion.chestPosition();
        if (chestPosition == null) {
            return;
        }
        Long2ObjectOpenHashMap<LongOpenHashSet> worldChunks =
                this.chestLinksByWorldChunk.get(chestPosition.worldKey());
        if (worldChunks == null) {
            return;
        }
        long chunkKey = chunkKey(chestPosition.blockX() >> 4, chestPosition.blockZ() >> 4);
        LongOpenHashSet ids = worldChunks.get(chunkKey);
        if (ids == null) {
            return;
        }
        ids.remove(minion.id().value());
        if (ids.isEmpty()) {
            worldChunks.remove(chunkKey);
        }
        if (worldChunks.isEmpty()) {
            this.chestLinksByWorldChunk.remove(chestPosition.worldKey());
        }
    }

    private void index(Minion minion) {
        String worldKey = minion.position().worldKey();
        long chunkKey = chunkKey(minion.position().blockX() >> 4, minion.position().blockZ() >> 4);
        this.minionsByWorldChunk
            .computeIfAbsent(worldKey, ignored -> new Long2ObjectOpenHashMap<>())
            .computeIfAbsent(chunkKey, ignored -> new LongOpenHashSet())
            .add(minion.id().value());
    }

    private void unindex(Minion minion) {
        String worldKey = minion.position().worldKey();
        Long2ObjectOpenHashMap<LongOpenHashSet> worldChunks = this.minionsByWorldChunk.get(worldKey);
        if (worldChunks == null) {
            return;
        }
        long chunkKey = chunkKey(minion.position().blockX() >> 4, minion.position().blockZ() >> 4);
        LongOpenHashSet ids = worldChunks.get(chunkKey);
        if (ids == null) {
            return;
        }
        ids.remove(minion.id().value());
        if (ids.isEmpty()) {
            worldChunks.remove(chunkKey);
        }
        if (worldChunks.isEmpty()) {
            this.minionsByWorldChunk.remove(worldKey);
        }
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) ^ (chunkZ & 0xFFFF_FFFFL);
    }
}
