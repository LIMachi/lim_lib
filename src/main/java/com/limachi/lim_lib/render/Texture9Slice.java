package com.limachi.lim_lib.render;

import com.limachi.lim_lib.maths.Box2d;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Texture9Slice {
    public ResourceLocation file;
    int x1, x2, x3, x4, y1, y2, y3, y4;
    public int fileWidth;
    public int fileHeight;
    TextureApplicationPattern sides = TextureApplicationPattern.STRETCH;
    TextureApplicationPattern inner = TextureApplicationPattern.STRETCH;

    @OnlyIn(Dist.CLIENT)
    public static Minecraft mc = Minecraft.getInstance();

    public Texture9Slice(ResourceLocation file, int x1, int x2, int x3, int x4, int y1, int y2, int y3, int y4) {
        this.file = file;
        this.x1 = x1;
        this.x2 = x2;
        this.x3 = x3;
        this.x4 = x4;
        this.y1 = y1;
        this.y2 = y2;
        this.y3 = y3;
        this.y4 = y4;
        this.fileWidth = 256;
        this.fileHeight = 256;
    }

    public Texture9Slice(ResourceLocation file, Box2d outer, Box2d inner, int fileWidth, int fileHeight) {
        this.file = file;
        this.x1 = (int)outer.getX1();
        this.x2 = (int)inner.getX1();
        this.x3 = (int)inner.getX2();
        this.x4 = (int)outer.getX2();
        this.y1 = (int)outer.getY1();
        this.y2 = (int)inner.getY1();
        this.y3 = (int)inner.getY2();
        this.y4 = (int)outer.getY2();
        this.fileWidth = fileWidth;
        this.fileHeight = fileHeight;
    }

    @OnlyIn(Dist.CLIENT)
    public Texture9Slice bindTexture() { mc.getTextureManager().bindForSetup(file); return this; }

    public Box2d getSlice(int x, int y) {
        Box2d out = new Box2d(0, 0, 0, 0);
        switch (x) {
            case 0 -> {
                out.setX1(x1);
                out.setX2(x2);
            }
            case 1 -> {
                out.setX1(x2);
                out.setX2(x3);
            }
            case 2 -> {
                out.setX1(x3);
                out.setX2(x4);
            }
        }
        switch (y) {
            case 0 -> {
                out.setY1(y1);
                out.setY2(y2);
            }
            case 1 -> {
                out.setY1(y2);
                out.setY2(y3);
            }
            case 2 -> {
                out.setY1(y3);
                out.setY2(y4);
            }
        }
        return out;
    }

    public void render(PoseStack stack, int blitOffset, Box2d to) {
        if (file != null) bindTexture();
        int tw = (int)to.getWidth();
        Box2d tb = getSlice(0, 0);
        if (tb.getHeight() > 0 && tb.getWidth() > 0) {
            tw -= tb.getWidth();
            RenderUtils.blitPattern(TextureApplicationPattern.TILE, stack, blitOffset, tb, tb.copy().setX1(to.getX1()).setY1(to.getY1()), fileWidth, fileHeight);
        }
//        if (tw > 0 && ) {
//
//        }
    }
}
