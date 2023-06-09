package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.Box2d;
import com.limachi.lim_lib.render.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class ScrollBarWidget extends BaseWidget<ScrollBarWidget> {

    public static final int WIDTH = 16;
    public static final int CURSOR_WIDTH = 12;
    public static final int BORDER = 2;
    public static final int DOUBLE_BORDER = BORDER * 2;
    public static final int CURSOR_HEIGHT = 15;
    public static final double HALF_CURSOR_AND_BORDER = CURSOR_HEIGHT / 2. + BORDER;
    public static final double HEIGHT_CLICK_OFFSET = HALF_CURSOR_AND_BORDER * 2;

    public static final ResourceLocation SCROLL_BAR_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/scroll_bar.png");
    public static final Box2d SCROLL_BAR_CUTOUT = new Box2d(WIDTH, RenderUtils.DEFAULT_FILE_HEIGHT);
    public static final Box2d SCROLL_BAR_CURSOR_POSITION = new Box2d(WIDTH, 0, CURSOR_WIDTH, CURSOR_HEIGHT);
    public static final Box2d SCROLL_BAR_CURSOR_PRESSED_POSITION = new Box2d(WIDTH, CURSOR_HEIGHT, CURSOR_WIDTH, CURSOR_HEIGHT);

    protected int min;
    protected int max;
    protected int step;
    protected int scroll;
    protected int prev;

    protected Consumer<ScrollBarWidget> onScrollChange;

    public ScrollBarWidget(@NotNull AnchoredBox box, int min, int max, int step, int scroll, Consumer<ScrollBarWidget> onScrollChange) {
        super(box, new WidgetOptions().catchMouseEvents(true).canTakeFocus(true).catchKeyboardEvents(true));
        setBackground(SCROLL_BAR_TEXTURE, SCROLL_BAR_CUTOUT);
        this.min = min;
        this.max = max;
        this.step = step;
        this.scroll = scroll;
        this.prev = scroll;
        this.onScrollChange = onScrollChange;
    }

    public int getMin() { return min; }

    public int getMax() { return max; }

    public int getStep() { return step; }

    @Override
    public void backRender(@NotNull PoseStack stack, double mouseX, double mouseY, float partialTick) {
        Box2d cursor = new Box2d(CURSOR_WIDTH, CURSOR_HEIGHT);
        cursor.setY1((((scroll - min) / (double)(max - min)) * (currentArea().getHeight() - cursor.getHeight() - DOUBLE_BORDER)) + currentArea().getY1() + BORDER);
        cursor.setX1(currentArea().getX1() + BORDER);
        RenderUtils.blitUnscaled(null, stack, 0, cursor, isDragged() || isOvered ? SCROLL_BAR_CURSOR_PRESSED_POSITION : SCROLL_BAR_CURSOR_POSITION);
    }

    protected int scrollClick(double mouseY) {
        double t = min + (max - min) * Mth.clamp((float)(relativeMouseY() - HALF_CURSOR_AND_BORDER) / (currentArea().getHeight() - HEIGHT_CLICK_OFFSET), 0f, 1f);
        if ((t - min) % step != 0) {
            int s = (int)Math.round((t - min) % step);
            int l = (int)Math.floor(((t - min) / step) * step + min);
            return l + (s >= step / 2. && l + step <= max ? step : 0);
        }
        return (int)t;
    }

    public ScrollBarWidget setMax(int value, boolean andCorrectCursorPosition) {
        max = value;
        if (scroll > max && andCorrectCursorPosition)
            setScroll(max, false, true);
        return this;
    }

    public ScrollBarWidget setMin(int value, boolean andCorrectCursorPosition) {
        min = value;
        if (scroll < min && andCorrectCursorPosition)
            setScroll(min, false, true);
        return this;
    }

    public ScrollBarWidget setStep(int value, boolean andCorrectCursorPosition) {
        step = value;
        if ((value - min) % step != 0 && andCorrectCursorPosition)
            setScroll(scroll, true, true);
        return this;
    }

    public ScrollBarWidget setScroll(int value) {
        setScroll(value, true, false);
        return this;
    }

    public ScrollBarWidget setScrollAndCallback(int value) {
        setScroll(value, true, true);
        return this;
    }

    protected void setScroll(int value, boolean applyClamping, boolean runCallbackIfChanged) {
        if (applyClamping) {
            if (value < min)
                value = min;
            if (value > max)
                value = max;
            if ((value - min) % step >= step / 2.)
                value += step;
        }
        prev = scroll;
        scroll = value;
        if (runCallbackIfChanged && prev != scroll && onScrollChange != null)
            onScrollChange.accept(this);
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (isOvered) {
            setScroll(scrollClick(mouseY), false, true);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) {
        if (isDragged()) {
            setScroll(scrollClick(mouseY), false, true);
            return true;
        }
        return false;
    }

    @Override
    protected boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        setScroll(Mth.clamp(scroll + (amount < 0 ? step : -step), min, max), false, true);
        return true;
    }

    public int getLastDelta() { return scroll - prev; }
    public int getScroll() { return scroll; }
}
