package com.limachi.lim_lib.render;

import com.limachi.lim_lib.ClientEvents;
import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.network.messages.QueueOverlayRendererMsg;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
//import net.minecraftforge.client.event.RenderLevelStageEvent; //VERSION 1.19.2
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.HashMap;

public class BlockRenderUtils extends RenderType {

    public static final int DEFAULT_OVERLAY = 0x22FF2255; //transparent green

//    public static final RenderType BLOCK_OVERLAY_RENDER_TYPE = create("BlockOverlay",
//            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
//            RenderType.CompositeState.builder()
//                    .setShaderState(RenderStateShard.ShaderStateShard.POSITION_COLOR_SHADER)
//                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
//                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
//                    .setTextureState(NO_TEXTURE)
//                    .setDepthTestState(LEQUAL_DEPTH_TEST)
//                    .setCullState(CULL)
//                    .setLightmapState(NO_LIGHTMAP)
//                    .setWriteMaskState(COLOR_DEPTH_WRITE)
//                    .createCompositeState(false));

    private static final HashMap<String, HashMap<BlockPos, Pair<Integer, Integer>>> CLIENT_OVERLAYS = new HashMap<>();

    public BlockRenderUtils(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static void queueOverlayRenderer(Level level, BlockPos pos, @Nullable Player player, int color, int ticks) {
        if (level.isClientSide)
            queueOverlayRenderer(level.dimension().location().toString(), pos, color, ticks);
        else {
            QueueOverlayRendererMsg msg = new QueueOverlayRendererMsg(level.dimension().location().toString(), pos, color, ticks);
            if (player == null)
                NetworkManager.toClients(LimLib.COMMON_ID, msg);
            else
                NetworkManager.toClient(LimLib.COMMON_ID, (ServerPlayer)player, msg);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void queueOverlayRenderer(String level, BlockPos pos, int color, int ticks) {
        if (!CLIENT_OVERLAYS.containsKey(level))
            CLIENT_OVERLAYS.put(level, new HashMap<>());
        CLIENT_OVERLAYS.get(level).put(pos, new Pair<>(ticks + ClientEvents.tick, color));
    }

    @OnlyIn(Dist.CLIENT)
    static void renderOverlayAt(Matrix4f mat, VertexConsumer builder, BlockPos pos, int color) {
        float red = ((color & 0xFF000000) >> 24) / 255f;
        float green = ((color & 0xFF0000) >> 16) / 255f;
        float blue = ((color & 0xFF00) >> 8) / 255f;
        float alpha = (color & 0xFF) / 255f;

        //down
        builder.vertex(mat, 0, 0, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 0, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 0, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 0, 0, 0).color(red, green, blue, alpha).endVertex();

        //up
        builder.vertex(mat, 0, 1, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 0, 1, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 1, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 1, -1).color(red, green, blue, alpha).endVertex();

        //east
        builder.vertex(mat, 0, 0, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 0, 1, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 1, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 0, -1).color(red, green, blue, alpha).endVertex();

        //west
        builder.vertex(mat, 0, 0, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 0, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 1, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 0, 1, 0).color(red, green, blue, alpha).endVertex();

        //south
        builder.vertex(mat, 1, 0, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 1, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 1, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 1, 0, 0).color(red, green, blue, alpha).endVertex();

        //north
        builder.vertex(mat, 0, 0, -1).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 0, 0, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 0, 1, 0).color(red, green, blue, alpha).endVertex();
        builder.vertex(mat, 0, 1, -1).color(red, green, blue, alpha).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    static void renderOverlays(
//            RenderLevelStageEvent //VERSION 1.19.2
            RenderLevelLastEvent //VERSION 1.18.2
                    event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        String levelName = mc.level.dimension().location().toString();
        HashMap<BlockPos, Pair<Integer, Integer>> overlays = CLIENT_OVERLAYS.get(levelName);
        if (overlays == null || overlays.isEmpty()) return;

        Vec3 cam = mc.gameRenderer.getMainCamera().getPosition();
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(-cam.x(), -cam.y(), -cam.z());

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderType.translucentNoCrumbling());

        overlays.replaceAll((pos, ticksColor)->{
            if (ticksColor == null || ClientEvents.tick > ticksColor.getFirst()) return null;

            pose.pushPose();
            pose.translate(pos.getX(), pos.getY(), pos.getZ());
            pose.translate(-0.005f, -0.005f, -0.005f);
            pose.scale(1.01f, 1.01f, 1.01f);
            pose.mulPose(Vector3f.YP.rotationDegrees(-90.0F));

            renderOverlayAt(pose.last().pose(), builder, pos, ticksColor.getSecond());
            pose.popPose();
            return ticksColor;
        });

        pose.popPose();
        RenderSystem.disableDepthTest();
        buffer.endBatch(RenderType.translucentNoCrumbling());
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class BlockRenderEvents {

        @SubscribeEvent
        static void renderLevelStageEvent(
//                RenderLevelStageEvent //VERSION 1.19.2
                RenderLevelLastEvent //VERSION 1.18.2
                event) {
//            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return; //VERSION 1.19.2
            renderOverlays(event);
        }
    }
}
