package com.limachi.lim_lib.render;

import com.limachi.lim_lib.maths.Box2d;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.client.gui.Font;

public class RenderUtils {
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

    public static void blitSquareUV(double x1, double x2, double y1, double y2, int blitOffset, float minU, float maxU, float minV, float maxV) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(x1, y2, blitOffset).uv(minU, maxV).endVertex();
        bufferbuilder.vertex(x2, y2, blitOffset).uv(maxU, maxV).endVertex();
        bufferbuilder.vertex(x2, y1, blitOffset).uv(maxU, minV).endVertex();
        bufferbuilder.vertex(x1, y1, blitOffset).uv(minU, minV).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }

    public static void blitPattern(TextureApplicationPattern pattern, PoseStack stack, int blitOffset, Box2d from, Box2d to, int fileWidth, int fileHeight) {
        if (from.getWidth() < 0.01 || from.getHeight() < 0.01 || to.getWidth() < 0.01 || to.getHeight() < 0.01) return;
        switch (pattern) {
            case MIDDLE_EXPANSION -> {
                double mx = to.getWidth() / from.getWidth();
                double my = to.getWidth() / from.getWidth();
                double mm = Math.max(mx, my);
                if (mm > 1) {
                    mx /= mm;
                    my /= mm;
                }
                Box2d tfrom = from.copy().scaleWidthAndHeight(mx / 2, my / 2);
                Box2d tto = to.copy().scaleWidthAndHeight(0.5, 0.5);
                blitPattern(TextureApplicationPattern.STRETCH, stack, blitOffset, tfrom, tto, fileWidth, fileHeight);
                blitPattern(TextureApplicationPattern.STRETCH, stack, blitOffset, tfrom.move(from.getWidth() - tfrom.getWidth(), 0), tto.move(from.getWidth() / 2, 0), fileWidth, fileHeight);
                blitPattern(TextureApplicationPattern.STRETCH, stack, blitOffset, tfrom.move(0, from.getHeight() - tfrom.getHeight()), tto.move(0, from.getHeight() / 2), fileWidth, fileHeight);
                blitPattern(TextureApplicationPattern.STRETCH, stack, blitOffset, tfrom.move(-(from.getWidth() - tfrom.getWidth()), 0), tto.move(-(from.getWidth() / 2), 0), fileWidth, fileHeight);
            }
            case STRETCH -> {
                Box2d t = from.copy().transform(stack.last().pose());
                blitSquareUV(t.getX1(), t.getX2(), t.getY1(), t.getY2(), blitOffset, (float)from.getX1() / (float)fileWidth, (float)from.getX2() / (float)fileWidth, (float)from.getY1() / (float)fileHeight, (float)from.getY2() / (float)fileHeight);
            }
            case TILE -> {
                float ratioX = (float)to.getWidth() / (float)from.getWidth();
                float ratioY = (float)to.getHeight() / (float)from.getHeight();
                for (float y = 0; y < ratioY; y += 1)
                    for (float x = 0; x < ratioX; x += 1)
                        blitSquareUV(to.getX1(), to.getX2(), to.getY1(), to.getY2(), blitOffset, (float)from.getX1() / (float)fileWidth, (float)from.getX2() / (float)fileWidth, (float)from.getY1() / (float)fileHeight, (float)from.getY2() / (float)fileHeight);
            }
        }
    }

    public static void blitPattern(int tint, TextureApplicationPattern pattern, PoseStack stack, int blitOffset, Box2d from, Box2d to, int fileWidth, int fileHeight) {
        blitPattern(pattern, stack, blitOffset, from, to, fileWidth, fileHeight);
        drawBox(stack, to, tint, blitOffset + 1);
    }
}
