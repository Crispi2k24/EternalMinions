package com.eternalcode.minions.render;

import com.eternalcode.minions.addon.MinionSkinConfig;
import com.eternalcode.minions.addon.MinionSkins;
import com.eternalcode.minions.config.MinionItemsConfig;
import com.eternalcode.minions.minion.Minion;
import com.eternalcode.minions.item.MinionAppearanceItems;
import com.eternalcode.minions.item.MinionTrimArmor;
import com.eternalcode.minions.minion.behavior.MinionBehavior;
import com.eternalcode.minions.minion.behavior.MinionBehaviorRegistry;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import me.tofaa.entitylib.meta.types.MannequinMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import me.tofaa.entitylib.wrapper.WrapperEntityEquipment;
import me.tofaa.entitylib.wrapper.WrapperLivingEntity;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class NpcMinionRenderer extends AbstractEntityLibMinionRenderer {

    private static final double DEFAULT_SCALE = 0.55D;

    private final MinionBehaviorRegistry behaviors;
    private final MinionAppearanceItems appearance;
    private final MinionSkins skins;
    private final MinionTrimArmor trimArmor;

    public NpcMinionRenderer(
        EntityLibHologramRenderer holograms,
        MinionEntityIndex entityIndex,
        MinionBehaviorRegistry behaviors,
        MinionAppearanceItems appearance,
        MinionSkins skins,
        MinionTrimArmor trimArmor
    ) {
        super(holograms, entityIndex);
        this.behaviors = behaviors;
        this.appearance = appearance;
        this.skins = skins;
        this.trimArmor = trimArmor;
    }

    @Override
    WrapperEntity createBody(Minion minion) {
        WrapperLivingEntity body = new WrapperLivingEntity(EntityTypes.MANNEQUIN);
        MannequinMeta meta = body.getEntityMeta(MannequinMeta.class);
        meta.setImmovable(true);
        body.setHasNoGravity(true);

        MinionBehavior behavior = this.behaviors.find(minion.behaviorId()).orElse(null);
        body.getAttributes().setAttribute(
            Attributes.SCALE,
            behavior == null ? DEFAULT_SCALE : behavior.config().npcScale
        );
        WrapperEntityEquipment equipment = body.getEquipment();
        if (behavior != null) {
            MinionSkinConfig minionSkin = this.skins.skin(minion).orElse(null);
            MinionItemsConfig items = minionSkin == null ? behavior.config().items : minionSkin.items;
            String headTexture = items.helmet.texture.isEmpty()
                ? behavior.config().items.helmet.texture
                : items.helmet.texture;
            String npcSkin = minionSkin != null && !minionSkin.npcSkin.isEmpty()
                ? minionSkin.npcSkin
                : behavior.config().npcSkin;
            String skin = npcSkin.isEmpty() ? headTexture : npcSkin;
            if (!skin.isEmpty()) {
                meta.setProfile(createSkinProfile(skin));
            }
            this.equipArmor(equipment, minion);
        }
        equipment.setMainHand(equipmentItem(minion.equipment().tool()));
        return body;
    }

    @Override
    void animate(long minionId, RenderedMinion minion, float targetYaw) {
        this.faceTarget(minion, targetYaw);
        ((WrapperLivingEntity) minion.body()).swingMainHand();
    }

    @Override
    void equipArmor(WrapperEntityEquipment equipment, Minion minion) {
        MinionBehavior behavior = this.behaviors.find(minion.behaviorId()).orElse(null);
        if (behavior == null) {
            return;
        }

        MinionItemsConfig items = this.skins.skin(minion).map(skin -> skin.items).orElse(behavior.config().items);
        ItemStack helmet = this.appearance.helmet(behavior.config(), items);
        if (helmet == null || helmet.getType() != Material.PLAYER_HEAD) {
            equipment.setHelmet(equipmentItem(helmet));
        }
        if (this.trimArmor.enabled()) {
            equipment.setChestplate(equipmentItem(this.trimArmor.chestplate(minion)));
            equipment.setLeggings(equipmentItem(this.trimArmor.leggings(minion)));
            equipment.setBoots(equipmentItem(this.trimArmor.boots(minion)));
            return;
        }
        equipment.setChestplate(equipmentItem(this.appearance.chestplate(items)));
        equipment.setLeggings(equipmentItem(this.appearance.leggings(items)));
        equipment.setBoots(equipmentItem(this.appearance.boots(items)));
    }

    private static ItemProfile createSkinProfile(String headTexture) {
        UUID profileId = UUID.nameUUIDFromBytes(headTexture.getBytes(StandardCharsets.UTF_8));
        return new ItemProfile("minion", profileId, List.of(new ItemProfile.Property("textures", headTexture, null)));
    }
}
