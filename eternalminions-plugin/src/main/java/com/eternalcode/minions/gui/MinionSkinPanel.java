package com.eternalcode.minions.gui;

import com.eternalcode.minions.access.MinionAccessAction;
import com.eternalcode.minions.addon.MinionSkinConfig;
import com.eternalcode.minions.addon.MinionSkinShop;
import com.eternalcode.minions.addon.MinionSkins;
import com.eternalcode.minions.config.MessagesConfig;
import com.eternalcode.minions.config.MinionPanelAction;
import com.eternalcode.minions.config.MinionPanelElementConfig;
import com.eternalcode.minions.minion.Minion;
import com.eternalcode.minions.minion.MinionLifecycleService;
import com.eternalcode.minions.minion.access.MinionAccessGuard;
import com.eternalcode.minions.notice.NoticeService;
import com.eternalcode.multification.notice.Notice;
import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class MinionSkinPanel {

    private static final int COLUMNS = 9;
    private static final int MAXIMUM_ROWS = 6;
    private static final int BACK_COLUMN = 4;

    private final Plugin plugin;
    private final MinionPanelConfig config;
    private final MessagesConfig messages;
    private final NoticeService notices;
    private final MiniMessage miniMessage;
    private final MinionSkins skins;
    private final MinionSkinShop shop;
    private final MinionAccessGuard access;
    private final MinionLifecycleService lifecycle;
    private final PanelItemFactory items;

    public MinionSkinPanel(
        Plugin plugin,
        MinionPanelConfig config,
        MessagesConfig messages,
        NoticeService notices,
        MiniMessage miniMessage,
        MinionSkins skins,
        MinionSkinShop shop,
        MinionAccessGuard access,
        MinionLifecycleService lifecycle
    ) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.notices = notices;
        this.miniMessage = miniMessage;
        this.skins = skins;
        this.shop = shop;
        this.access = access;
        this.lifecycle = lifecycle;
        this.items = new PanelItemFactory(miniMessage);
    }

    public void open(Player player, Minion minion, Runnable back) {
        this.access.findAccessible(
                player,
                minion.id(),
                MinionAccessAction.MANAGE
        ).ifPresent(current -> this.openAccessible(player, current, back));
    }

    private void openAccessible(Player player, Minion minion, Runnable back) {
        int entries = this.skins.config().skins.size() + 1;
        int rows = Math.min(MAXIMUM_ROWS, Math.ceilDiv(entries, COLUMNS) + 1);
        ChestGui gui = new ChestGui(
            rows,
            ComponentHolder.of(this.miniMessage.deserialize(this.config.skinsTitle)
                .decoration(TextDecoration.ITALIC, false)),
            this.plugin
        );
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        gui.setOnGlobalDrag(event -> event.setCancelled(true));

        StaticPane pane = new StaticPane(COLUMNS, rows);
        gui.addPane(Slot.fromIndex(0), pane);
        this.populate(new View(gui, pane, player, back, rows), minion);
        gui.show(player);
    }

    private void populate(View view, Minion minion) {
        view.pane().clear();
        int capacity = (view.rows() - 1) * COLUMNS;

        view.pane().addItem(this.skinItem(view, minion, null, this.config.skinDefault, true, Map.of()), 0, 0);
        int index = 1;
        for (Map.Entry<String, MinionSkinConfig> entry : this.skins.config().skins.entrySet()) {
            if (index >= capacity) {
                break;
            }
            boolean owned = this.shop.owns(view.player(), entry.getKey());
            view.pane().addItem(
                this.skinItem(
                    view,
                    minion,
                    entry.getKey(),
                    icon(entry.getValue()),
                    owned,
                    Map.of("{SKIN_PRICE}", this.shop.price(entry.getValue()))
                ),
                index % COLUMNS,
                index / COLUMNS
            );
            index++;
        }

        view.pane().addItem(new GuiItem(
            this.items.create(this.config.upgradesBack, Map.of()),
            event -> view.back().run(),
            this.plugin
        ), BACK_COLUMN, view.rows() - 1);
    }

    private GuiItem skinItem(
        View view,
        Minion minion,
        String skinId,
        MinionPanelElementConfig icon,
        boolean owned,
        Map<String, String> placeholders
    ) {
        boolean wearing = Objects.equals(minion.equipment().skinId(), skinId);
        List<String> lore = new ArrayList<>(icon.lore);
        lore.addAll(wearing ? this.config.skinWornLore : owned ? this.config.skinOwnedLore : this.config.skinLockedLore);

        MinionPanelElementConfig shown = copy(icon);
        shown.glowing = wearing;
        return new GuiItem(this.items.create(shown, lore, placeholders), event -> {
            if (wearing) {
                return;
            }
            if (!owned && !this.buy(view.player(), skinId, icon.displayName)) {
                return;
            }
            this.access.findAccessible(view.player(), minion.id(), MinionAccessAction.MANAGE).ifPresent(current -> {
                Minion updated = current.withEquipment(current.equipment().withSkin(skinId));
                this.lifecycle.updateSkin(updated, view.player().getUniqueId());
                this.populate(view, updated);
                view.gui().update();
            });
        }, this.plugin);
    }

    private boolean buy(Player player, String skinId, String skinName) {
        return switch (this.shop.purchase(player, skinId)) {
            case PURCHASED -> {
                this.notices.create()
                    .viewer(player)
                    .notice(this.messages.skinPurchased)
                    .placeholder("{SKIN}", skinName)
                    .send();
                yield true;
            }
            case TOO_POOR -> this.refuse(player, this.messages.skinCannotAfford);
            case NOT_READY -> this.refuse(player, this.messages.skinNotReady);
            case UNKNOWN -> false;
        };
    }

    private boolean refuse(Player player, Notice notice) {
        this.notices.create().viewer(player).notice(notice).send();
        return false;
    }

    private static MinionPanelElementConfig icon(MinionSkinConfig skin) {
        MinionPanelElementConfig icon = new MinionPanelElementConfig();
        icon.action = MinionPanelAction.NONE;
        icon.material = skin.icon;
        icon.displayName = skin.displayName;
        icon.lore = skin.lore;
        return icon;
    }

    private static MinionPanelElementConfig copy(MinionPanelElementConfig source) {
        MinionPanelElementConfig copy = new MinionPanelElementConfig();
        copy.action = source.action;
        copy.material = source.material;
        copy.amount = source.amount;
        copy.displayName = source.displayName;
        copy.lore = source.lore;
        copy.glowing = source.glowing;
        copy.customModelData = source.customModelData;
        copy.hideTooltip = source.hideTooltip;
        return copy;
    }

    private record View(ChestGui gui, StaticPane pane, Player player, Runnable back, int rows) {
    }
}
