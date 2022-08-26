package com.limachi.lim_lib.render;

import com.limachi.lim_lib.maths.Box2d;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TextureCutout {

    public static final int SELECTED = 1;
    public static final int HOVERED = 2;

    @OnlyIn(Dist.CLIENT)
    public static Minecraft mc = Minecraft.getInstance();

    public ResourceLocation file;
    public int tint;
    public Box2d corners;
    public int fileWidth;
    public int fileHeight;

    public void setTint(int tint) { this.tint = tint; }

    public TextureCutout(ResourceLocation file, int fileWidth, int fileHeight, double x, double y, double w, double h) {
        this(file, fileWidth, fileHeight, new Box2d(x, y, w, h));
    }

    public TextureCutout(ResourceLocation file, double x, double y, double w, double h) { this(file, 256, 256, x, y, w, h); }

    public TextureCutout(ResourceLocation file, Box2d cutout) { this(file, 256, 256, cutout); }

    public TextureCutout(ResourceLocation file, int fileWidth, int fileHeight, Box2d cutout) {
        this.file = file;
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
        this.corners = cutout;
    }

    @OnlyIn(Dist.CLIENT)
    public TextureCutout bindTexture() { mc.getTextureManager().bindForSetup(file); return this; }

    public TextureCutout copy() { return new TextureCutout(file, fileWidth, fileHeight, corners.getX1(), corners.getY1(), corners.getWidth(), corners.getHeight()); }

    public TextureCutout setCorners(Box2d corners) { this.corners = corners; return this; }

    @OnlyIn(Dist.CLIENT)
    public void blit(PoseStack matrixStack, Box2d coords, int blitOffset, TextureApplicationPattern pattern) {
        if (file != null) bindTexture();
        RenderUtils.blitPattern(tint, pattern, matrixStack, blitOffset, corners, coords, fileWidth, fileHeight);
    }

    @OnlyIn(Dist.CLIENT)
    public void blitButton(PoseStack matrixStack, Box2d coords, int blitOffset, TextureApplicationPattern pattern, int state) {
        if (file != null) bindTexture();
        if (state != 0) {
            double dx = (state & SELECTED) == SELECTED ? corners.getWidth() : 0.;
            double dy = (state & HOVERED) == HOVERED ? corners.getHeight() : 0.;
            corners = corners.move(dx, dy);
            RenderUtils.blitPattern(tint, pattern, matrixStack, blitOffset, corners, coords, fileWidth, fileHeight);
            corners = corners.move(-dx, -dy);
        } else
            RenderUtils.blitPattern(tint, pattern, matrixStack, blitOffset, corners, coords, fileWidth, fileHeight);
    }
}
