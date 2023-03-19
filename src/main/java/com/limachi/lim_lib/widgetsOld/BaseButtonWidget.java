package com.limachi.lim_lib.widgetsOld;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.maths.Box2d;
import com.limachi.lim_lib.render.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
@OnlyIn(Dist.CLIENT)
public abstract class BaseButtonWidget<T extends BaseButtonWidget<T>> extends BaseWidget<T> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/button.png");
    public static final Box2d IDLE_CUTOUT = new Box2d(128, 128);
    public static final Box2d PRESSED_CUTOUT = new Box2d(128, 0, 128, 128);
    public static final Box2d HOVERED_CUTOUT = new Box2d(0, 128, 128, 128);
    public static final Box2d PRESSED_HOVERED_CUTOUT = new Box2d(128, 128, 128, 128);
    public static final Box2d[] CUTOUTS = {IDLE_CUTOUT, PRESSED_CUTOUT, HOVERED_CUTOUT, PRESSED_HOVERED_CUTOUT};

    protected Component title;
    protected int titleColor = 0xFFFFFFFF;
    protected Font titleFont = Minecraft.getInstance().font;

    protected boolean hoverState = false;
    protected boolean pressedState = false;

    protected Consumer<T> onStateChange = null;

    public BaseButtonWidget(int x, int y, int w, int h, Component title) {
        super(x, y, w, h, BACKGROUND, IDLE_CUTOUT, false);
        this.title = title;
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

    protected void updateHoverState(boolean state) {
        if (state && !hoverState) {
            hoverState = true;
            backgroundCutout = pressedState ? PRESSED_HOVERED_CUTOUT : HOVERED_CUTOUT;
        } else if (!state && hoverState) {
            hoverState = false;
            backgroundCutout = pressedState ? PRESSED_CUTOUT : IDLE_CUTOUT;
        }
    }

    protected void updatePressedState(boolean state, int button) {
        if (state && !pressedState) {
            pressedState = true;
            backgroundCutout = hoverState ? PRESSED_HOVERED_CUTOUT : PRESSED_CUTOUT;
            if (onStateChange != null)
                onStateChange.accept((T)this);
        } else if (!state && pressedState) {
            pressedState = false;
            backgroundCutout = hoverState ? HOVERED_CUTOUT : IDLE_CUTOUT;
            if (onStateChange != null)
                onStateChange.accept((T)this);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        boolean t = isMouseOver(mouseX, mouseY);
        updateHoverState(t);
        if (!t && pressedState)
            updatePressedState(false, 0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        boolean t = isMouseOver(mouseX, mouseY);
        updatePressedState(t, button);
        return t;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        boolean t = pressedState;
        updatePressedState(false, button);
        return t;
    }

    @Override
    public void renderRelative(PoseStack stack, int mouseX, int mouseY, float partialTick, boolean isMouseOver) {
        double y = (area.getHeight() - titleFont.lineHeight) / 2.;
        RenderUtils.drawString(stack, titleFont, title.getString(), area.copy().setX1(2).setY1(y), titleColor, true, false);
    }

    public boolean getPressedState() { return pressedState; }
}
