package com.eternalcode.minions.addon;

import com.eternalcode.minions.bridge.vault.EconomyService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public final class MinionSkinShop implements Listener {

    private final Plugin plugin;
    private final MinionSkinsConfig config;
    private final MinionSkinOwnerRepository repository;
    private final EconomyService economy;
    private final Map<UUID, Set<String>> owned = new HashMap<>();

    public MinionSkinShop(
        Plugin plugin,
        MinionSkinsConfig config,
        MinionSkinOwnerRepository repository,
        EconomyService economy
    ) {
        this.plugin = plugin;
        this.config = config;
        this.repository = repository;
        this.economy = economy;
    }

    public void loadOnlinePlayers() {
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            this.load(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.owned.remove(event.getPlayer().getUniqueId());
    }

    public boolean owns(Player player, String skinId) {
        return player.hasPermission(this.config.permissionPrefix + skinId)
            || this.owned.getOrDefault(player.getUniqueId(), Set.of()).contains(skinId);
    }

    public String price(MinionSkinConfig skin) {
        return skin.price.signum() <= 0 ? "0" : this.economy.format(skin.price);
    }

    public PurchaseResult purchase(Player player, String skinId) {
        MinionSkinConfig skin = this.config.skins.get(skinId);
        if (skin == null) {
            return PurchaseResult.UNKNOWN;
        }

        UUID playerId = player.getUniqueId();
        Set<String> skins = this.owned.get(playerId);
        if (skins == null) {
            return PurchaseResult.NOT_READY;
        }
        if (skin.price.signum() > 0 && !this.economy.withdraw(playerId, skin.price)) {
            return PurchaseResult.TOO_POOR;
        }

        skins.add(skinId);
        this.repository.grant(playerId, skinId).exceptionally(failure -> {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to save skin " + skinId + " of " + playerId, failure);
            return null;
        });
        return PurchaseResult.PURCHASED;
    }

    private void load(Player player) {
        UUID playerId = player.getUniqueId();
        this.repository.ownedBy(playerId).thenAccept(skins -> this.plugin.getServer().getScheduler().runTask(
            this.plugin,
            () -> {
                if (player.isOnline()) {
                    this.owned.put(playerId, new HashSet<>(skins));
                }
            }
        )).exceptionally(failure -> {
            this.plugin.getLogger().log(Level.SEVERE, "Unable to load skins of " + playerId, failure);
            return null;
        });
    }

    public enum PurchaseResult {
        PURCHASED,
        TOO_POOR,
        NOT_READY,
        UNKNOWN
    }
}
