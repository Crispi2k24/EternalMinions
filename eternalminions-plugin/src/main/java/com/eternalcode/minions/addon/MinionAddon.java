package com.eternalcode.minions.addon;

public record MinionAddon(MinionAddonType type, String id) {

    public MinionAddon {
        if (type == null || id == null || id.isBlank()) {
            throw new IllegalArgumentException("Addon type and id are required");
        }
    }
}
