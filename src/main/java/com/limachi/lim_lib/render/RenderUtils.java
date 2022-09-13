package com.limachi.lim_lib.render;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.maths.Box2d;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderUtils {
    public static final int DEFAULT_FILE_WIDTH = 256;
    public static final int DEFAULT_FILE_HEIGHT = 256;
    public static final Box2d FULL_FILE_CUTOUT = new Box2d(DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT);
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/background.png");
    public static final ResourceLocation PLAYER_SLOTS_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/player_slots.png");
    public static final Box2d PLAYER_SLOTS_CUTOUT = new Box2d(162, 76);
    public static final Box2d PLAYER_SLOTS_WITHOUT_BELT_CUTOUT = new Box2d(162, 54);
    public static final ResourceLocation SLOTS_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/slots.png");
    public static final ResourceLocation LOCKED_SLOTS_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/locked_slots.png");

    /**
     * color order: 0xAARRGGBB
     */

    public static Vector4f expandColor(int color, boolean isShadow) {
        float shadow = isShadow ? 0.25f : 1.0f;
        return new Vector4f(
                (float)(color >> 16 & 255) / 255.0F * shadow,
                (float)(color >> 8 & 255) / 255.0F * shadow,
                (float)(color & 255) / 255.0F * shadow,
                (float)(color >> 24 & 255) / 255.0F);
    }

    /**
     * color order: 0xAARRGGBB
     */
    public static int compactColor(Vector4f color) {
        return ((int)(color.w() * 255) << 24) | ((int)(color.x() * 255) << 16) | ((int)(color.y() * 255) << 8) | (int)(color.z() * 255);
    }

    public static void drawBox(PoseStack matrixStack, Box2d box, int color, int depth) {
        if (box.getHeight() < 0.01 || box.getWidth() < 0.01) return;
        Matrix4f matrix = matrixStack.last().pose();
        Vector4f ec = expandColor(color, false);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(matrix, (float)box.getX1(), (float)box.getY2(), depth).color(ec.x(), ec.y(), ec.z(), ec.w()).endVertex();
        bufferbuilder.vertex(matrix, (float)box.getX2(), (float)box.getY2(), depth).color(ec.x(), ec.y(), ec.z(), ec.w()).endVertex();
        bufferbuilder.vertex(matrix, (float)box.getX2(), (float)box.getY1(), depth).color(ec.x(), ec.y(), ec.z(), ec.w()).endVertex();
        bufferbuilder.vertex(matrix, (float)box.getX1(), (float)box.getY1(), depth).color(ec.x(), ec.y(), ec.z(), ec.w()).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static int getPrintedStringWidth(Matrix4f matrix, Font font, String text) {
        return (int)(font.width(text) * Float.parseFloat(matrix.toString().substring(10).split(" ")[0]));
    }

    public static void drawString(PoseStack matrixStack, Font font, String string, Box2d coords, int textColor, boolean withShadow, boolean withWrap) {
        if (withShadow)
            drawString(matrixStack, font, string, coords.copy().move(1, 1), compactColor(expandColor(textColor, true)), false, withWrap);
        int r = 0;
        float x = (float)coords.getX1();
        float y = (float)coords.getY1();
        int l = 0;
        String tmpStr;

        if (withWrap)
            while (r < string.length() && y + l * font.lineHeight < coords.getY2()) {
                tmpStr = font.plainSubstrByWidth(string.substring(r), (int) coords.getWidth());
                if (tmpStr.isEmpty())
                    tmpStr = string.substring(0, 1);
                r += tmpStr.length();
                font.draw(matrixStack, tmpStr, x, y + l * font.lineHeight, textColor);
                ++l;
            }
        else
            font.draw(matrixStack, string, x, y, textColor);
    }

    public static void blitMiddleExp(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack) { blitMiddleExp(screen, stack, null, null, null, DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT); }
    public static void blitMiddleExp(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Box2d from) { blitMiddleExp(screen, stack, null, null, from, DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT); }
    public static void blitMiddleExp(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Integer depth, @Nullable Box2d to, @Nullable Box2d from) { blitMiddleExp(screen, stack, depth, to, from, DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT); }
    public static void blitMiddleExp(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Integer depth, @Nullable Box2d to, @Nullable Box2d from, int fileWidth, int fileHeight) {
        if (screen != null) {
            if (to != null)
                to = to.copy().move(screen.getGuiLeft(), screen.getGuiTop());
            else
                to = new Box2d(screen.getGuiLeft(), screen.getGuiTop(), screen.getXSize(), screen.getYSize());
            if (depth == null)
                depth = screen.getBlitOffset();
        }
        if (to == null) return;
        if (from == null)
            from = new Box2d(fileWidth, fileHeight);
        if (depth == null) depth = 0;
        Box2d quadrant = to.copy().scaleWidthAndHeight(0.5, 0.5);
        GuiComponent.blit(stack, (int)quadrant.getX1(), (int)quadrant.getY1(), depth, (float)from.getX1(), (float)from.getY1(), (int)quadrant.getWidth(), (int)quadrant.getHeight(), fileWidth, fileHeight);
        quadrant.move(quadrant.getWidth(), 0.);
        GuiComponent.blit(stack, (int)quadrant.getX1(), (int)quadrant.getY1(), depth, (float)(from.getX2() - quadrant.getWidth()), (float)from.getY1(), (int)quadrant.getWidth(), (int)quadrant.getHeight(), fileWidth, fileHeight);
        quadrant.move(0., quadrant.getHeight());
        GuiComponent.blit(stack, (int)quadrant.getX1(), (int)quadrant.getY1(), depth, (float)(from.getX2() - quadrant.getWidth()), (float)(from.getY2() - quadrant.getHeight()), (int)quadrant.getWidth(), (int)quadrant.getHeight(), fileWidth, fileHeight);
        quadrant.move(-quadrant.getWidth(), 0.);
        GuiComponent.blit(stack, (int)quadrant.getX1(), (int)quadrant.getY1(), depth, (float)from.getX1(), (float)(from.getY2() - quadrant.getHeight()), (int)quadrant.getWidth(), (int)quadrant.getHeight(), fileWidth, fileHeight);
    }

    public static void blitUnscaled(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack) { blitUnscaled(screen, stack, null, null, null, DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT); }
    public static void blitUnscaled(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Box2d to) { blitUnscaled(screen, stack, null, to, null, DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT); }
    public static void blitUnscaled(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Integer depth, @Nullable Box2d to) { blitUnscaled(screen, stack, depth, to, null, DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT); }
    public static void blitUnscaled(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Integer depth, @Nullable Box2d to, @Nullable Vec2 from) { blitUnscaled(screen, stack, depth, to, from, DEFAULT_FILE_WIDTH, DEFAULT_FILE_HEIGHT); }
    public static void blitUnscaled(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Integer depth, @Nullable Box2d to, @Nullable Vec2 from, int fileWidth, int fileHeight) {
        if (screen != null) {
            if (to != null)
                to = to.copy().move(screen.getGuiLeft(), screen.getGuiTop());
            else
                to = new Box2d(screen.getGuiLeft(), screen.getGuiTop(), screen.getXSize(), screen.getYSize());
            if (depth == null)
                depth = screen.getBlitOffset();
        }
        if (to == null) return;
        if (from == null) from = Vec2.ZERO;
        if (depth == null) depth = 0;
        GuiComponent.blit(stack, (int)to.getX1(), (int)to.getY1(), depth, from.x, from.y, (int)to.getWidth(), (int)to.getHeight(), fileWidth, fileHeight);
    }

    public static void playerSlots(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, int x, int y, boolean with_belt) { playerSlots(screen, stack, null, x, y, with_belt); }
    public static void playerSlots(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Integer depth, int x, int y, boolean with_belt) {
        RenderSystem.setShaderTexture(0, PLAYER_SLOTS_TEXTURE);
        Box2d tob = with_belt ? PLAYER_SLOTS_CUTOUT.copy() : PLAYER_SLOTS_WITHOUT_BELT_CUTOUT.copy();
        blitUnscaled(screen, stack, depth, tob.setX1(x).setY1(y), Vec2.ZERO, 256, 256);
    }

    public static void background(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack) {
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
        blitMiddleExp(screen, stack);
    }

    public static void slots(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, int x, int y, int rows, int columns, int active) { slots(screen, stack, null, x, y, rows, columns, active); }
    public static void slots(@Nullable AbstractContainerScreen<?> screen, @Nonnull PoseStack stack, @Nullable Integer depth, int x, int y, int rows, int columns, int active) {
        if (rows * columns <= 0) return;
        if (active > 0) {
            RenderSystem.setShaderTexture(0, SLOTS_TEXTURE);
            Box2d to = new Box2d(x, y, columns * 18, rows * 18);
            blitUnscaled(screen, stack, depth, to);
        }
        if (active < rows * columns) {
            RenderSystem.setShaderTexture(0, LOCKED_SLOTS_TEXTURE);
            if (active % columns != 0) {
                blitUnscaled(screen, stack, depth, new Box2d(x + 18 * (active % columns), y + 18 * (active / columns), 18 * (columns - (active % columns)), 18));
                if (active / columns + 1 < rows)
                    blitUnscaled(screen, stack, depth, new Box2d(x, y + 18 * (active / columns + 1), columns * 18, 18 * (rows - (active / columns + 1))));
            } else
                blitUnscaled(screen, stack, depth, new Box2d(x, y + 18 * (active / columns), columns * 18, 18 * (rows - (active / columns))));
        }
    }

    public static void entity(LivingEntity entity, int x, int y) { entity(entity, x, y, 30, x, y); }
    public static void entity(LivingEntity entity, int x, int y, int scale) { entity(entity, x, y, scale, x, y); }
    public static void entity(LivingEntity entity, int x, int y, float lookAtX, float lookAtY) { entity(entity, x, y, 30, lookAtX, lookAtY); }
    public static void entity(LivingEntity entity, int x, int y, int scale, float lookAtX, float lookAtY) {
        InventoryScreen.renderEntityInInventory(x, y, scale, lookAtX, lookAtY, entity);
    }

    public static void entity(AbstractContainerScreen<?> screen, LivingEntity entity, int x, int y) { entity(screen, entity, x, y, 30, x, y); }
    public static void entity(AbstractContainerScreen<?> screen, LivingEntity entity, int x, int y, int scale) { entity(screen, entity, x, y, scale, x, y); }
    public static void entity(AbstractContainerScreen<?> screen, LivingEntity entity, int x, int y, float lookAtX, float lookAtY) { entity(screen, entity, x, y, 30, lookAtX, lookAtY); }
    public static void entity(AbstractContainerScreen<?> screen, LivingEntity entity, int x, int y, int scale, float lookAtX, float lookAtY) {
        InventoryScreen.renderEntityInInventory(x + screen.getGuiLeft(), y + screen.getGuiTop(), scale, lookAtX + screen.getGuiLeft(), lookAtY + screen.getGuiTop(), entity);
    }
}
