package com.eternalcode.minions.minion.behavior.impl.killer;

import com.eternalcode.minions.config.AbstractMinionConfig;
import com.eternalcode.minions.minion.status.MinionStatus;
import com.eternalcode.minions.minion.tool.ToolCategory;
import com.eternalcode.minions.minion.upgrade.DefaultUpgradeKinds;
import com.eternalcode.minions.minion.upgrade.MinionUpgrades;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Include;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.entity.EntityType;

@Include(AbstractMinionConfig.class)
public final class KillerConfig extends AbstractMinionConfig {

    @Override
    public Path resolve(Path dataDirectory) {
        return dataDirectory.resolve("minions").resolve("killer.yml");
    }

    @Comment({
            "When enabled, Sweeping Edge is required to damage every eligible mob in range.",
            "Without the enchantment, the killer attacks only the nearest eligible mob."
    })
    public boolean requireSweepingEdgeForAreaDamage = true;

    @Comment({
            "Mob types the killer is allowed to attack.",
            "When empty and attackAllMonstersWhenEmpty is enabled,",
            "the killer attacks every entity implementing the Monster interface."
    })
    public List<EntityType> allowedMobs = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON
    );

    @Comment({
            "When enabled, an empty allowedMobs list permits every hostile monster.",
            "When disabled, an empty list prevents the killer from attacking anything."
    })
    public boolean attackAllMonstersWhenEmpty = true;

    @Comment("Prevents the killer from attacking mobs with a custom name.")
    public boolean ignoreNamedMobs = true;

    @Comment("Prevents the killer from attacking invulnerable entities.")
    public boolean ignoreInvulnerableMobs = true;

    @Comment("Base attack range before applying the range upgrade.")
    public int attackRangeBlocks = 4;

    @Comment("Ticks between attacks.")
    public int attackCooldownTicks = 20;

    @Comment({
            "Base damage used before applying weapon attribute modifiers.",
            "Minecraft players normally have 1 base attack damage."
    })
    public double baseAttackDamage = 1.0D;

    @Comment("Horizontal knockback applied per Knockback enchantment level.")
    public double knockbackStrengthPerLevel = 0.4D;

    @Comment("Vertical knockback applied when the weapon has Knockback.")
    public double knockbackVerticalStrength = 0.15D;

    public KillerConfig() {
        this.displayName =
                "<color:#F11919:#FF3F3F:#F11919>ᴢᴀʙᴏᴊᴄᴀ";

        this.tool.category = ToolCategory.WEAPON;
        this.tool.required = true;

        this.items.helmet.texture =
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc1MzJlOTBjNTczYTM5NGM3ODAyYWE0MTU4MzA1ODAyYjU5ZTY3ZjJhMmI3ZTNmZDAzNjNhYTZlYTQyYjg0MSJ9fX0=";

        this.npcSkin =
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHBzOi8vcy5uYW1lbWMuY29tL2kvOWMxOTM4MDVmMWY4OTQ2OC5wbmcifX19";

        this.items.setLeatherArmorColor(
                Color.fromRGB(241, 25, 25)
        );

        this.statuses = defaultStatuses();

        this.usageInstructions.displayName = "<green>Jak używać";
        this.usageInstructions.lore = List.of(
                "<gray>Atakuje moby w promieniu <aqua>4 <gray>bloków.",
                "",
                "<gray>• Włóż <aqua>broń <gray>w pole narzędzia.",
                "<gray>• Postaw go przy spawnerze albo farmie mobów.",
                "<gray>• Zaklęcia miecza działają jak u gracza,",
                "<gray>  <aqua>Grabież <gray>daje więcej łupu.",
                "",
                "<dark_gray>Nazwanych i nieśmiertelnych mobów",
                "<dark_gray>nie rusza."
        );
    }

    public int attackRange(MinionUpgrades upgrades) {
        int baseRange = Math.max(
                1,
                this.attackRangeBlocks
        );

        return this.upgradeTierValueOrHigher(
                upgrades,
                DefaultUpgradeKinds.RANGE,
                baseRange
        );
    }

    public int attackCooldown() {
        return Math.max(
                1,
                this.attackCooldownTicks
        );
    }

    public double baseAttackDamage() {
        return Math.max(
                0.0D,
                this.baseAttackDamage
        );
    }

    public int attackTargetLimit(
            int nearbyEntityCount,
            int sweepingEdgeLevel
    ) {
        if (nearbyEntityCount <= 0) {
            return 0;
        }
        if (
                this.requireSweepingEdgeForAreaDamage
                        && sweepingEdgeLevel <= 0
        ) {
            return 1;
        }
        return nearbyEntityCount;
    }

    private static Map<MinionStatus, String> defaultStatuses() {
        Map<MinionStatus, String> statuses =
                new LinkedHashMap<>();

        statuses.put(
                KillerStatuses.ATTACKING,
                "<green>Atakuje..."
        );

        statuses.put(
                KillerStatuses.NO_ENEMIES,
                "<gray>Brak celów w zasięgu"
        );

        statuses.put(
                KillerStatuses.PROTECTED_MOBS_NEARBY,
                "<gray>W pobliżu są tylko chronione moby"
        );

        statuses.put(
                KillerStatuses.NO_WEAPON,
                "<red>Brakuje broni"
        );

        return statuses;
    }
}
