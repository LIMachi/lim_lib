package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchorPoint;
import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.IVec2i;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class TextWidget extends BaseWidget<TextWidget> {

    protected Component text;
    protected Font font = Minecraft.getInstance().font;
    protected int color = 0xFFFFFFFF;
    protected boolean hasShadow = false;

    public TextWidget(@NotNull AnchoredBox box, Component text) {
        super(box, new WidgetOptions());
        this.text = text;
    }

    public TextWidget setText(@Nonnull Component text) {
        this.text = text;
        return this;
    }

    public TextWidget setFont(@Nonnull Font font) {
        this.font = font;
        return this;
    }

    public TextWidget setColor(int color) {
        this.color = color;
        return this;
    }

    public TextWidget setShadow(boolean hasShadow) {
        this.hasShadow = hasShadow;
        return this;
    }

    public Component getText() { return text; }

    public Font getFont() { return font; }

    public int getColor() { return color; }

    public void drawCenteredString(PoseStack stack, Font font, Component text, int x, int y, int color) {
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        if (hasShadow)
            font.drawShadow(stack, formattedcharsequence, (float)(x - font.width(formattedcharsequence) / 2), (float)y, color);
        else
            font.draw(stack, formattedcharsequence, (float)(x - font.width(formattedcharsequence) / 2), (float)y, color);
    }

    @Override
    public void backRender(@NotNull PoseStack stack, double mouseX, double mouseY, float partialTick) {
        IVec2i v = AnchorPoint.CENTER.anchor((int)currentArea().getWidth(), (int)currentArea().getHeight());
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        float x = (float)(v.x() - font.width(formattedcharsequence) / 2 + currentArea().getX1());
        float y = (float)(v.y() - font.lineHeight / 2 + currentArea().getY1());
        if (hasShadow)
            font.drawShadow(stack, formattedcharsequence, x, y, color);
        else
            font.draw(stack, formattedcharsequence, x, y, color);
    }
}
