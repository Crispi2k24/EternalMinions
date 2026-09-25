package com.eternalcode.minions.addon;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "eternal_minion_skin_owners")
public final class MinionSkinOwnerTable {

    public static final String PLAYER_COLUMN = "player_uuid";

    @DatabaseField(columnName = "id", id = true) private String id;
    @DatabaseField(columnName = PLAYER_COLUMN, canBeNull = false, index = true) private String playerId;
    @DatabaseField(columnName = "skin_id", canBeNull = false) private String skinId;

    public MinionSkinOwnerTable() {
    }

    public MinionSkinOwnerTable(String playerId, String skinId) {
        this.id = playerId + ":" + skinId;
        this.playerId = playerId;
        this.skinId = skinId;
    }

    public String skinId() {return this.skinId;}
}
