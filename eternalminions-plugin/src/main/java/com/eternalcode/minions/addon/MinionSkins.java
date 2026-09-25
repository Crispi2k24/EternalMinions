package com.eternalcode.minions.addon;

import com.eternalcode.minions.minion.Minion;
import java.util.Optional;

public final class MinionSkins {

    private final MinionSkinsConfig config;

    public MinionSkins(MinionSkinsConfig config) {
        this.config = config;
    }

    public Optional<MinionSkinConfig> skin(Minion minion) {
        String skinId = minion.equipment().skinId();
        return skinId == null ? Optional.empty() : Optional.ofNullable(this.config.skins.get(skinId));
    }

    public MinionSkinsConfig config() {
        return this.config;
    }
}
