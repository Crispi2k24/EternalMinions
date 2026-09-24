package com.eternalcode.minions.economy;

import org.bukkit.plugin.Plugin;

public interface MinionEconomyService {

    MinionEconomyRegistration registerProvider(
            Plugin plugin,
            MinionEconomyProvider provider
    );
}
