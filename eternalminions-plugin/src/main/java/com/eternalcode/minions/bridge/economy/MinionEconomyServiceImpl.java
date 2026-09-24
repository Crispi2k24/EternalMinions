package com.eternalcode.minions.bridge.economy;

import com.eternalcode.minions.bridge.vault.EconomyService;
import com.eternalcode.minions.economy.MinionEconomyProvider;
import com.eternalcode.minions.economy.MinionEconomyRegistration;
import com.eternalcode.minions.economy.MinionEconomyService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public final class MinionEconomyServiceImpl implements MinionEconomyService, EconomyService, Listener {

    private final Logger logger;
    private final Optional<? extends EconomyService> builtInEconomy;

    private volatile Registration externalProvider;

    public MinionEconomyServiceImpl(Logger logger, Optional<? extends EconomyService> builtInEconomy) {
        this.logger = logger;
        this.builtInEconomy = builtInEconomy;
    }

    @Override
    public MinionEconomyRegistration registerProvider(Plugin plugin, MinionEconomyProvider provider) {
        if (!plugin.isEnabled()) {
            throw new IllegalStateException(
                    "Cannot register an economy provider for disabled plugin: " + plugin.getName()
            );
        }

        Registration current = this.externalProvider;

        if (current != null && current.plugin != plugin) {
            throw new IllegalStateException(
                    "An economy provider is already registered by: " + current.plugin.getName()
            );
        }

        Registration registration = new Registration(plugin, provider);
        this.externalProvider = registration;

        return registration;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        Registration registration = this.externalProvider;

        if (registration != null && registration.plugin == event.getPlugin()) {
            this.externalProvider = null;
        }
    }

    @Override
    public boolean available() {
        return this.resolveProvider() != null || this.builtInAvailable();
    }

    @Override
    public boolean withdraw(UUID playerId, BigDecimal amount) {
        MinionEconomyProvider provider = this.resolveProvider();

        if (provider == null) {
            return this.builtInAvailable() && this.builtInEconomy.get().withdraw(playerId, amount);
        }

        try {
            return provider.withdraw(playerId, amount);
        }
        catch (RuntimeException exception) {
            this.logger.log(
                    Level.WARNING,
                    "Economy provider failed to withdraw " + amount + " from " + playerId,
                    exception
            );

            return false;
        }
    }

    @Override
    public String format(BigDecimal amount) {
        MinionEconomyProvider provider = this.resolveProvider();

        if (provider == null) {
            return this.builtInAvailable() ? this.builtInEconomy.get().format(amount) : amount.toPlainString();
        }

        try {
            return provider.format(amount);
        }
        catch (RuntimeException exception) {
            this.logger.log(
                    Level.WARNING,
                    "Economy provider failed to format " + amount,
                    exception
            );

            return amount.toPlainString();
        }
    }

    private MinionEconomyProvider resolveProvider() {
        Registration registration = this.externalProvider;

        if (registration != null
                && registration.plugin.isEnabled()
                && this.isAvailable(registration.provider)) {
            return registration.provider;
        }

        return null;
    }

    private boolean builtInAvailable() {
        return this.builtInEconomy.map(EconomyService::available).orElse(false);
    }

    private boolean isAvailable(MinionEconomyProvider provider) {
        try {
            return provider.available();
        }
        catch (RuntimeException exception) {
            this.logger.log(
                    Level.WARNING,
                    "Economy provider failed to report availability",
                    exception
            );

            return false;
        }
    }

    private final class Registration implements MinionEconomyRegistration {

        private final Plugin plugin;
        private final MinionEconomyProvider provider;

        private Registration(Plugin plugin, MinionEconomyProvider provider) {
            this.plugin = plugin;
            this.provider = provider;
        }

        @Override
        public void unregister() {
            if (this.isRegistered()) {
                MinionEconomyServiceImpl.this.externalProvider = null;
            }
        }

        @Override
        public boolean isRegistered() {
            return MinionEconomyServiceImpl.this.externalProvider == this;
        }
    }
}
