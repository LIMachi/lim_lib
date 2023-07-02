package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.maths.AnchorPoint;
import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.Box2d;
import com.limachi.lim_lib.maths.IVec2i;
import com.limachi.lim_lib.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/*
public class BaseButtonWidget<T extends BaseButtonWidget<T>> extends BaseWidget<T> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/button.png");
    public static final Box2d IDLE_CUTOUT = new Box2d(128, 128);
    public static final Box2d PRESSED_CUTOUT = new Box2d(128, 0, 128, 128);
    public static final Box2d HOVERED_CUTOUT = new Box2d(0, 128, 128, 128);
    public static final Box2d PRESSED_HOVERED_CUTOUT = new Box2d(128, 128, 128, 128);
    public static final Box2d[] CUTOUTS = {IDLE_CUTOUT, PRESSED_CUTOUT, HOVERED_CUTOUT, PRESSED_HOVERED_CUTOUT};

    protected Component title;
    protected int titleColor = 0xFFFFFFFF;
    protected Font titleFont = Minecraft.getInstance().font;

    protected boolean pressedState = false;

    protected Consumer<T> onStateChange;

    protected BaseButtonWidget(@NotNull AnchoredBox box, Component title, Consumer<T> onStateChange) {
        super(box, new WidgetOptions().catchMouseEvents(true));
        this.title = title;
        this.onStateChange = onStateChange;
        setBackground(BACKGROUND, IDLE_CUTOUT);
    }

    public T setOnStateChange(@Nullable Consumer<T> onStateChange) {
        this.onStateChange = onStateChange;
        return (T)this;
    }

    public T setFont(@Nonnull Font font) {
        titleFont = font;
        return (T)this;
    }

    public T setTitle(@Nonnull Component title) {
        this.title = title;
        return (T)this;
    }

    public T setTitleColor(int color) {
        titleColor = color;
        return (T)this;
    }

    @Override
    protected void onMouseStartOver(double mouseX, double mouseY) {
        if (pressedState)
            backgroundCutout = PRESSED_HOVERED_CUTOUT;
        else
            backgroundCutout = HOVERED_CUTOUT;
    }

    @Override
    protected void onMouseStopOver(double mouseX, double mouseY) {
        if (pressedState)
            backgroundCutout = PRESSED_CUTOUT;
        else
            backgroundCutout = IDLE_CUTOUT;
    }

    protected void updatePressedState(boolean state, int button, boolean andUpdate) {
        if (state && !pressedState) {
            pressedState = true;
            backgroundCutout = isOvered ? PRESSED_HOVERED_CUTOUT : PRESSED_CUTOUT;
            if (onStateChange != null)
                onStateChange.accept((T)this);
        } else if (!state && pressedState) {
            pressedState = false;
            backgroundCutout = isOvered ? HOVERED_CUTOUT : IDLE_CUTOUT;
            if (onStateChange != null)
                onStateChange.accept((T)this);
        }
    }

    public boolean getPressedState() { return pressedState; }

    @Override
    public void backRender(@NotNull PoseStack stack, double mouseX, double mouseY, float partialTick) {
        IVec2i v = AnchorPoint.CENTER.anchor((int)currentArea().getWidth(), (int)currentArea().getHeight());
        FormattedCharSequence formattedcharsequence = title.getVisualOrderText();
        float x = (float)(v.x() - titleFont.width(formattedcharsequence) / 2 + currentArea().getX1());
        float y = (float)(v.y() - titleFont.lineHeight / 2 + currentArea().getY1());
        titleFont.drawShadow(stack, formattedcharsequence, x, y, titleColor);
    }
}
*/