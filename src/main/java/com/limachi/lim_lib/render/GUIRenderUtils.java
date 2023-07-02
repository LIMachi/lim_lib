package com.limachi.lim_lib.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class GUIRenderUtils {

    public static final Minecraft MINECRAFT = Minecraft.getInstance();
    public static final ItemRenderer ITEM_RENDERER = MINECRAFT.getItemRenderer();
    public static final TextureManager TEXTURE_MANAGER = MINECRAFT.getTextureManager();

    /**
     * Derived from ItemRenderer#renderGuiItem and ItemRenderer#renderGuiItemDecorations to also enable resize (via w and h) and color correction
     * @param stack the stack to render
     * @param x the horizontal gui position (starting from left == 0)
     * @param y the vertical gui position (starting from top == 0)
     * @param w the width of the item (vanilla default for a slot is 16)
     * @param h the height of the item (vanilla default for a slot is 16)
     * @param color color multiplier in ARGB order, set to 0XFFFFFFFF to use the default item color
     * @param decorate also render decorations (item count, damage bar, effects, etc...), note that a item count <= 0 will be rendered in red (as opposed to the vanilla that skips <= 0 counts)
     */

    /*
    public static void renderItem(ItemStack stack, int x, int y, int w, int h, int color, boolean decorate) {
        if (w <= 0 || h <= 0) return;
        BakedModel model = ITEM_RENDERER.getModel(stack, null, null, 0);
        TEXTURE_MANAGER.getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(255f / ((color & 0xFF0000) >> 16), 255f / ((color & 0xFF00) >> 8), 255f / (color & 0xFF), 255f / ((color & 0xFF000000) >> 24));
        PoseStack pose = RenderSystem.getModelViewStack();
        pose.pushPose();
        pose.translate(x + w / 2d, y + h / 2d, 100 + ITEM_RENDERER.blitOffset);
        pose.scale(w, -h, Math.max(w, h));
        RenderSystem.applyModelViewMatrix();
        MultiBufferSource.BufferSource buff = MINECRAFT.renderBuffers().bufferSource();
        boolean flatLight = !model.usesBlockLight();
        if (flatLight)
            Lighting.setupForFlatItems();
        ITEM_RENDERER.render(stack, ItemTransforms.TransformType.GUI, false, new PoseStack(), buff, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, model);
        buff.endBatch();
        RenderSystem.enableDepthTest();
        if (flatLight)
            Lighting.setupFor3DItems();
        pose.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        if (decorate) {
            PoseStack posestack = new PoseStack();
            if (stack.getCount() != 1) {
                String s = String.valueOf(stack.getCount());
                posestack.translate(0, 0, ITEM_RENDERER.blitOffset + 200);
                MultiBufferSource.BufferSource buff2 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                MINECRAFT.font.drawInBatch(s, (float)(x + w + 1 - MINECRAFT.font.width(s)), (float)(y + h - 5), stack.getCount() <= 0 ? ChatFormatting.RED.getColor() : ChatFormatting.WHITE.getColor(), true, posestack.last().pose(), buff2, false, 0, LightTexture.FULL_BRIGHT);
                buff2.endBatch();
            }
            if (stack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                int i = stack.getBarWidth();
                int j = stack.getBarColor();
                fillRect(bufferbuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
                fillRect(bufferbuilder, x + 2, y + 13, (int)(i * (w / 16d)), 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }
            LocalPlayer localplayer = Minecraft.getInstance().player;
            float f = localplayer == null ? 0 : localplayer.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator1 = Tesselator.getInstance();
                BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                fillRect(bufferbuilder1, x, y + Mth.floor(h * (1 - f)), w, Mth.ceil(h * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            net.minecraftforge.client.ItemDecoratorHandler.of(stack).render(MINECRAFT.font, stack, x, y, ITEM_RENDERER.blitOffset); //the scalling will probably fail here, we might be hable to hack something using the PoseStack
        }
    }*/

    /**
     * similar to GUIRenderUtils#renderItem but for fluids
     */

    /*
    public static void renderFluid(FluidStack stack, int x, int y, int w, int h, int color, boolean decorate) {
        Fluid fluid = stack.getFluid();
        if (w <= 0 || h <= 0 || fluid.isSame(Fluids.EMPTY)) return;
        TextureAtlasSprite sprite = MINECRAFT.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(stack.getFluid()).getStillTexture());
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShaderColor(255f / ((color & 0xFF0000) >> 16), 255f / ((color & 0xFF00) >> 8), 255f / (color & 0xFF), 255f / ((color & 0xFF000000) >> 24));
        BufferUploader.drawWithShader(tiledSquareUVVertex(RenderSystem.getModelViewStack().last().pose(), x, y, w, h, sprite.getWidth(), sprite.getHeight(), sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), ITEM_RENDERER.blitOffset + 100).end());
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
        if (decorate) {
            PoseStack posestack = new PoseStack();
            String s;
            int a = Math.abs(stack.getAmount());
            if (a < 1000)
                s = a + Component.translatable("gui.fluid.milli-bucket").getString();
            else if (a > 1000000)
                s = (int)Math.floor(a / 1000000d) + Component.translatable("gui.fluid.kilo-bucket").getString() + (int)Math.floor(a / 1000d) % 1000;
            else
                s = (int)Math.floor(a / 1000d) + Component.translatable("gui.fluid.bucket").getString() + ((a % 1000 > 0) ? (a % 1000) : "");
            if (stack.getAmount() < 0)
                s = "-" + s;
            posestack.translate(0, 0, ITEM_RENDERER.blitOffset + 200);
            MultiBufferSource.BufferSource buff = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            MINECRAFT.font.drawInBatch(s, (float)(x + w + 1 - MINECRAFT.font.width(s)), (float)(y + h - 5), stack.getAmount() <= 0 ? ChatFormatting.RED.getColor() : ChatFormatting.WHITE.getColor(), true, posestack.last().pose(), buff, false, 0, LightTexture.FULL_BRIGHT);
            buff.endBatch();
        }
    }*/

    public static BufferBuilder tiledSquareUVVertex(Matrix4f mat, int x, int y, int w, int h, int sw, int sh, float u0, float v0, float u1, float v1, float zOffset) {
        BufferBuilder buff = Tesselator.getInstance().getBuilder();
        buff.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        int i = x;
        for (; i + sw <= x + w; i += sw) {
            int j = y;
            for (; j + sh <= y + h; j += sh) {
                buff.vertex(mat, i, j + sh, zOffset).uv(u0, v1).endVertex();
                buff.vertex(mat, i + sw, j + sh, zOffset).uv(u1, v1).endVertex();
                buff.vertex(mat, i + sw, j, zOffset).uv(u1, v0).endVertex();
                buff.vertex(mat, i, j, zOffset).uv(u0, v0).endVertex();
            }
            if (j < y + h) {
                int ly = y + h - j;
                float v = v0 + (v1 - v0) * (ly / (float)sh);
                buff.vertex(mat, i, ly, zOffset).uv(u0, v).endVertex();
                buff.vertex(mat, i + sw, ly, zOffset).uv(u1, v).endVertex();
                buff.vertex(mat, i + sw, j, zOffset).uv(u1, v0).endVertex();
                buff.vertex(mat, i, j, zOffset).uv(u0, v0).endVertex();
            }
        }
        if (i < x + w) {
            int lx = x + w - i;
            float u = u0 + (u1 - u0) * (lx / (float)sw);
            int j = y;
            for (; j + sh <= h; j += sh) {
                buff.vertex(mat, i, j + sh, zOffset).uv(u0, v1).endVertex();
                buff.vertex(mat, lx, j + sh, zOffset).uv(u, v1).endVertex();
                buff.vertex(mat, lx, j, zOffset).uv(u, v0).endVertex();
                buff.vertex(mat, i, j, zOffset).uv(u0, v0).endVertex();
            }
            if (j < y + h) {
                int ly = y + h - j;
                float v = v0 + (v1 - v0) * (ly / (float)sh);
                buff.vertex(mat, i, ly, zOffset).uv(u0, v).endVertex();
                buff.vertex(mat, lx, ly, zOffset).uv(u, v).endVertex();
                buff.vertex(mat, lx, j, zOffset).uv(u, v0).endVertex();
                buff.vertex(mat, i, j, zOffset).uv(u0, v0).endVertex();
            }
        }
        return buff;
    }

    private static void fillRect(BufferBuilder p_115153_, int p_115154_, int p_115155_, int p_115156_, int p_115157_, int p_115158_, int p_115159_, int p_115160_, int p_115161_) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        p_115153_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + 0), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        p_115153_.vertex((double)(p_115154_ + p_115156_), (double)(p_115155_ + 0), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        BufferUploader.drawWithShader(p_115153_.end());
    }
}
