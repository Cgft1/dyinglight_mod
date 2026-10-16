package com.dyinglight.entity;
import com.dyinglight.DyingLightMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
public class InfectedEntity extends ZombieEntity {
    public static final EntityType<InfectedEntity> TYPE = Registry.register(
        Registries.ENTITY_TYPE, new Identifier(DyingLightMod.MOD_ID, "infected"),
        EntityType.Builder.create(InfectedEntity::new, net.minecraft.entity.SpawnGroup.MONSTER)
            .setDimensions(0.6f, 1.95f).maxTrackingRange(8).build("infected"));
    public InfectedEntity(EntityType<? extends ZombieEntity> type, World world) { super(type, world); this.experiencePoints = 5; }
    public static DefaultAttributeContainer.Builder createInfectedAttributes() {
        return ZombieEntity.createZombieAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 28.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.27).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }
}
