package com.eternalcode.minions.gui;

import com.eternalcode.minions.access.MinionAccessAction;
import com.eternalcode.minions.addon.MinionSkinConfig;
import com.eternalcode.minions.addon.MinionSkins;
import com.eternalcode.minions.config.MinionPanelAction;
import com.eternalcode.minions.config.MinionPanelElementConfig;
import com.eternalcode.minions.minion.Minion;
import com.eternalcode.minions.minion.MinionLifecycleService;
import com.eternalcode.minions.minion.access.MinionAccessGuard;
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
    private final MiniMessage miniMessage;
    private final MinionSkins skins;
    private final MinionAccessGuard access;
    private final MinionLifecycleService lifecycle;
    private final PanelItemFactory items;

    public MinionSkinPanel(
        Plugin plugin,
        MinionPanelConfig config,
        MiniMessage miniMessage,
        MinionSkins skins,
        MinionAccessGuard access,
        MinionLifecycleService lifecycle
    ) {
        this.plugin = plugin;
        this.config = config;
        this.miniMessage = miniMessage;
        this.skins = skins;
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
        this.populate(gui, pane, player, minion, back, rows);
        gui.show(player);
    }

    private void populate(ChestGui gui, StaticPane pane, Player player, Minion minion, Runnable back, int rows) {
        pane.clear();
        int capacity = (rows - 1) * COLUMNS;

        pane.addItem(this.skinItem(gui, pane, player, minion, back, rows, null, this.config.skinDefault, true), 0, 0);
        int index = 1;
        for (Map.Entry<String, MinionSkinConfig> entry : this.skins.config().skins.entrySet()) {
            if (index >= capacity) {
                break;
            }
            MinionPanelElementConfig icon = icon(entry.getValue());
            boolean owned = this.skins.owns(player, entry.getKey());
            pane.addItem(
                this.skinItem(gui, pane, player, minion, back, rows, entry.getKey(), icon, owned),
                index % COLUMNS,
                index / COLUMNS
            );
            index++;
        }

        pane.addItem(new GuiItem(
            this.items.create(this.config.upgradesBack, Map.of()),
            event -> back.run(),
            this.plugin
        ), BACK_COLUMN, rows - 1);
    }

    private GuiItem skinItem(
        ChestGui gui,
        StaticPane pane,
        Player player,
        Minion minion,
        Runnable back,
        int rows,
        String skinId,
        MinionPanelElementConfig icon,
        boolean owned
    ) {
        boolean wearing = Objects.equals(minion.equipment().skinId(), skinId);
        List<String> lore = new ArrayList<>(icon.lore);
        lore.addAll(wearing ? this.config.skinWornLore : owned ? this.config.skinOwnedLore : this.config.skinLockedLore);

        MinionPanelElementConfig shown = copy(icon);
        shown.glowing = wearing;
        return new GuiItem(this.items.create(shown, lore, Map.of()), event -> {
            if (wearing || !owned) {
                return;
            }
            this.access.findAccessible(player, minion.id(), MinionAccessAction.MANAGE).ifPresent(current -> {
                Minion updated = current.withEquipment(current.equipment().withSkin(skinId));
                this.lifecycle.updateSkin(updated, player.getUniqueId());
                this.populate(gui, pane, player, updated, back, rows);
                gui.update();
            });
        }, this.plugin);
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
}
