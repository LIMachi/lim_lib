package com.limachi.lim_lib.tests;

import com.limachi.lim_lib.client.annotations.RegisterEntityRenderer;

import com.limachi.lim_lib.common.annotations.EntityAttributeBuilder;
import com.limachi.lim_lib.common.annotations.RegisterEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.architectury.registry.registries.RegistrySupplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class TestEntity extends Mob {
    @RegisterEntity(width = 0.5f, height = 0.5f)
    public static RegistrySupplier<EntityType<TestEntity>> R_TYPE;

    @EntityAttributeBuilder
    public static AttributeSupplier.Builder attributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.FOLLOW_RANGE, 16.);
    }

    public TestEntity(EntityType<? extends Mob> entityType, Level level) { super(entityType, level); }

    @RegisterEntityRenderer
    @Environment(EnvType.CLIENT)
    public static class TestEntityRenderer extends MobRenderer<TestEntity, TestEntityModel> {
        public static final float SHADOW_SIZE = 0.5f;
        public static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.fromNamespaceAndPath("lim_lib", "test_entity");

        public TestEntityRenderer(EntityRendererProvider.Context context) { super(context, new TestEntityModel(), SHADOW_SIZE); }

        @Override
        public @NotNull ResourceLocation getTextureLocation(TestEntity entity) { return TEXTURE_LOCATION; }
    }

    @Environment(EnvType.CLIENT)
    public static class TestEntityModel extends EntityModel<TestEntity> {
        public static final int TEXTURE_WIDTH = 16;
        public static final int TEXTURE_HEIGHT = 16;
        private static ModelPart root = null;
        private final ModelPart body;

        public TestEntityModel() {
            if (root == null) {
                MeshDefinition mesh = new MeshDefinition();
                mesh.getRoot().addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4f, 16f, -4f, 8f, 8f, 8f), PartPose.ZERO);
                root = mesh.getRoot().bake(TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }
            body = root.getChild("body");
        }

        @Override
        public void setupAnim(TestEntity entity, float f, float g, float h, float i, float j) {
            body.setRotation(0, entity.getYRot(), 0);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color) {
            body.render(poseStack, vertexConsumer, light, overlay, color);
        }
    }
}
