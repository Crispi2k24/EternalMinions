package com.eternalcode.minions.addon;

import com.eternalcode.commons.scheduler.Scheduler;
import com.eternalcode.minions.database.DatabaseManager;
import com.eternalcode.minions.database.repository.AbstractRepositoryOrmLite;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class MinionSkinOwnerRepository extends AbstractRepositoryOrmLite {

    public MinionSkinOwnerRepository(DatabaseManager databaseManager, Scheduler scheduler) {
        super(databaseManager, scheduler);
    }

    public CompletableFuture<Void> initialize() {
        return this.createTable(MinionSkinOwnerTable.class);
    }

    public CompletableFuture<Set<String>> ownedBy(UUID playerId) {
        return this.<MinionSkinOwnerTable, String, Set<String>>action(MinionSkinOwnerTable.class, dao -> {
            Set<String> owned = new HashSet<>();
            for (MinionSkinOwnerTable row : dao.queryForEq(MinionSkinOwnerTable.PLAYER_COLUMN, playerId.toString())) {
                owned.add(row.skinId());
            }
            return Set.copyOf(owned);
        });
    }

    public CompletableFuture<Void> grant(UUID playerId, String skinId) {
        return this.save(MinionSkinOwnerTable.class, new MinionSkinOwnerTable(playerId.toString(), skinId))
            .thenApply(status -> null);
    }
}
