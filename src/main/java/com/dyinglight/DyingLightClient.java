package com.dyinglight;
import com.dyinglight.entity.InfectedEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
public class DyingLightClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        EntityRendererRegistry.register(InfectedEntity.TYPE, context ->
            new MobEntityRenderer<InfectedEntity, ZombieEntityModel<InfectedEntity>>(
                context, new ZombieEntityModel<>(context.getPart(EntityModelLayers.ZOMBIE)), 0.5f) {
                @Override public Identifier getTexture(InfectedEntity entity) {
                    return new Identifier("minecraft", "textures/entity/zombie/zombie.png");
                }
            });
    }
}
