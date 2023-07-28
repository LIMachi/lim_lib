package com.limachi.lim_lib.render;

import com.limachi.lim_lib.LimLib;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * new version of the render utils and gui render utils using the new vanilla systems
 * (instances of GuiGraphics being passed as arg of most screen/widget functions)
 */
@OnlyIn(Dist.CLIENT)
public class GuiUtils {
    private static final Minecraft mc = Minecraft.getInstance();
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/background.png");
    public static final ResourceLocation SLOTS_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/slots.png");
    public static final ResourceLocation LOCKED_SLOTS_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/locked_slots.png");
    private static Pair<Integer, Integer> topLeft = null;
    private static Pair<Integer, Integer> area = null;

    public static void setGuiTopLeft(int x, int y) { topLeft = new Pair<>(x, y); }
    public static void setGuiArea(int w, int h) { area = new Pair<>(w, h); }

    public static void clearGuiTopLeft() { topLeft = null; }
    public static void clearGuiArea() { area = null; }

    public static int getGuiTop() {
        if (topLeft != null)
            return topLeft.getSecond();
        if (mc.screen instanceof AbstractContainerScreen<?> cs)
            return cs.getGuiTop();
        return 0;
    }

    public static int getGuiLeft() {
        if (topLeft != null)
            return topLeft.getFirst();
        if (mc.screen instanceof AbstractContainerScreen<?> cs)
            return cs.getGuiLeft();
        return 0;
    }

    public static int getGuiWidth() {
        if (area != null)
            return area.getFirst();
        if (mc.screen instanceof AbstractContainerScreen<?> cs)
            return cs.getXSize();
        if (mc.screen != null)
            return mc.screen.width;
        return 256;
    }

    public static int getGuiHeight() {
        if (area != null)
            return area.getSecond();
        if (mc.screen instanceof AbstractContainerScreen<?> cs)
            return cs.getYSize();
        if (mc.screen != null)
            return mc.screen.height;
        return 256;
    }

    public static double scaleX() {
        Window win = mc.getWindow();
        return win.getGuiScaledWidth() / (double)win.getScreenWidth();
    }

    public static double scaleY() {
        Window win = mc.getWindow();
        return win.getGuiScaledHeight() / (double)win.getScreenHeight();
    }

    public static int mouseX() {
        return (int)(mc.mouseHandler.xpos() * scaleX());
    }

    public static int mouseY() {
        return (int)(mc.mouseHandler.ypos() * scaleY());
    }

    public static void nineSlices(GuiGraphics gui, ResourceLocation texture, ScreenRectangle at, ScreenRectangle from, int border) {
        nineSlices(gui, texture, at, from, border, border, border, border);
    }

    public static void nineSlices(GuiGraphics gui, ResourceLocation texture, ScreenRectangle at, ScreenRectangle from, int borderWidth, int borderHeight) {
        nineSlices(gui, texture, at, from, borderWidth, borderHeight, borderWidth, borderHeight);
    }
    public static void nineSlices(GuiGraphics gui, ResourceLocation texture, ScreenRectangle at, ScreenRectangle from, int topLeftWidth, int topLeftHeight, int bottomRightWidth, int bottomRightHeight) {
        gui.blitNineSliced(texture, at.left() + getGuiLeft(), at.top() + getGuiTop(), at.width(), at.height(), topLeftWidth, topLeftHeight, bottomRightWidth, bottomRightHeight, from.width(), from.height(), from.left(), from.top());
    }

    public static void nineSlices(GuiGraphics gui, ResourceLocation texture, ScreenRectangle at, ScreenRectangle from, ScreenRectangle innerFrom) {
        nineSlices(gui, texture, at, from, innerFrom.left() - from.left(), innerFrom.top() - from.top(), from.right() - innerFrom.right(), from.bottom() - innerFrom.bottom());
    }

    public static void background(GuiGraphics gui) { background(gui, BACKGROUND_TEXTURE); }

    public static void background(GuiGraphics gui, ResourceLocation texture) {
        int width = getGuiWidth();
        int height = getGuiHeight();
        int left = (gui.guiWidth() - width) / 2;
        int top = (gui.guiHeight() - height) / 2;
        gui.blitNineSliced(texture, left, top, width, height, 8, 256, 256, 0, 0);
    }

    public static void slots(GuiGraphics gui, int x, int y, int columns, int rows, boolean locked) {
        gui.blit(locked ? LOCKED_SLOTS_TEXTURE : SLOTS_TEXTURE, x + getGuiLeft(), y + getGuiTop(), 0, 0, 18 * columns, 18 * rows, 256, 256);
    }

    public static void string(GuiGraphics gui, ScreenRectangle at, Component string, int color) {
        string(gui, mc.font, at, false, string, color);
    }

    public static void string(GuiGraphics gui, ScreenRectangle at, boolean leftJustified, Component string, int color) {
        string(gui, mc.font, at, leftJustified, string, color);
    }

    public static void string(GuiGraphics gui, Font font, ScreenRectangle at, Component string, int color) {
        string(gui, font, at, false, string, color);
    }

    /**
     * should make the scrolling system faster/easier to read (instead of back en forth, wait at the start, go to end then reset)
     */
    public static void string(GuiGraphics gui, Font font, ScreenRectangle at, boolean leftJustified, Component string, int color) {
        int pixelLength = font.width(string);
        ScreenRectangle cat = new ScreenRectangle(new ScreenPosition(at.left() + getGuiLeft(), at.top() + getGuiTop()), at.width(), at.height());
        int centeredHeight = (cat.top() + cat.bottom() - 9) / 2 + 1;
        if (pixelLength > cat.width()) {
            int cut = pixelLength - cat.width();
            double d0 = (double) Util.getMillis() / 1000.0D;
            double d1 = Math.max((double)cut * 0.5D, 3.0D);
            double d2 = Math.sin((Math.PI / 2D) * Math.cos((Math.PI * 2D) * d0 / d1)) / 2.0D + 0.5D;
            double d3 = Mth.lerp(d2, 0.0D, cut);
            gui.enableScissor(cat.left(), cat.top(), cat.right(), cat.bottom());
            gui.drawString(font, string, cat.left() - (int)d3, centeredHeight, color);
            gui.disableScissor();
        } else if (leftJustified)
            gui.drawString(font, string, cat.left(), centeredHeight, color);
        else
            gui.drawCenteredString(font, string, cat.left() + cat.width() / 2, centeredHeight, color);
    }

    /**
     * should make a version that use scisors and an area
     */
    public static void entity(GuiGraphics gui, LivingEntity entity, int x, int y) {
        int gx = x + getGuiLeft();
        int gy = y + getGuiTop();
        int scale = (int)(30. + (entity.getBbWidth() - 0.6) * -15.); //derived from player and horse
        InventoryScreen.renderEntityInInventoryFollowsMouse(gui, gx, gy, scale, gx - (float)mouseX(), gy - (float)mouseY() - 50f, entity);
    }
}
