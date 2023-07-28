package com.limachi.lim_lib.widgetsOld;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.maths.Box2d;
import com.limachi.lim_lib.render.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/*
@SuppressWarnings({"unused", "UnusedReturnValue"})
@OnlyIn(Dist.CLIENT)
public class ScrollBarWidget extends BaseWidget<ScrollBarWidget> {

    public static final int WIDTH = 16;
    public static final int CURSOR_WIDTH = 12;
    public static final int BORDER = 2;
    public static final int DOUBLE_BORDER = BORDER * 2;
    public static final int CURSOR_HEIGHT = 15;
    public static final double HALF_CURSOR_AND_BORDER = CURSOR_HEIGHT / 2. + BORDER;
    public static final double HEIGHT_CLICK_OFFSET = HALF_CURSOR_AND_BORDER * 2;

    public static final ResourceLocation SCROLL_BAR_TEXTURE = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/widgets.png");
    public static final Box2d SCROLL_BAR_CUTOUT = new Box2d(WIDTH, RenderUtils.DEFAULT_FILE_HEIGHT);
    public static final Vec2 SCROLL_BAR_CURSOR_VOLUME = new Vec2(CURSOR_WIDTH, CURSOR_HEIGHT);
    public static final Vec2 SCROLL_BAR_CURSOR_POSITION = new Vec2(WIDTH, 0);
    public static final Vec2 SCROLL_BAR_CURSOR_PRESSED_POSITION = new Vec2(WIDTH, CURSOR_HEIGHT);

    protected int min;
    protected int max;
    protected int step;
    protected int scroll;
    protected int prev;
    protected Consumer<ScrollBarWidget> onScrollCHange;

    public ScrollBarWidget(int x, int y, int height, int min, int max, int scroll, Consumer<ScrollBarWidget> onScrollCHange) {
        this(x, y, height, min, max, min <= max ? 1 : -1, scroll, onScrollCHange);
    }

    public ScrollBarWidget(int x, int y, int height, int min, int max, Consumer<ScrollBarWidget> onScrollCHange) {
        this(x, y, height, min, max, min <= max ? 1 : -1, min, onScrollCHange);
    }

    public ScrollBarWidget(int x, int y, int height, int min, int max, int step, int scroll, Consumer<ScrollBarWidget> onScrollCHange) {
        super(x, y, WIDTH, height, SCROLL_BAR_TEXTURE, SCROLL_BAR_CUTOUT, false);
        this.min = min;
        this.max = max;
        this.step = step;
        this.scroll = scroll;
        this.prev = scroll;
        this.onScrollCHange = onScrollCHange;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTick) {
        super.render(stack, mouseX, mouseY, partialTick);
        Box2d cursor = new Box2d(SCROLL_BAR_CURSOR_VOLUME);
        cursor.setY1((((scroll - min) / (double)(max - min)) * (area.getHeight() - cursor.getHeight() - DOUBLE_BORDER)) + area.getY1() + BORDER);
        cursor.setX1(area.getX1() + BORDER);
//        RenderUtils.blitUnscaled(getScreen(), stack, getBlitOffset(), cursor, isDragged() || isMouseOverBox(mouseX, mouseY, cursor) ? SCROLL_BAR_CURSOR_PRESSED_POSITION : SCROLL_BAR_CURSOR_POSITION);
    }

    protected int scrollClick(double mouseY) {
        double t = min + (max - min) * Mth.clamp((float)(relativeMouseOverY(mouseY) - HALF_CURSOR_AND_BORDER) / (area.getHeight() - HEIGHT_CLICK_OFFSET), 0f, 1f);
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
        if (runCallbackIfChanged && prev != scroll && onScrollCHange != null)
            onScrollCHange.accept(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (isMouseOver(mouseX, mouseY)) {
            setScroll(scrollClick(mouseY), false, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double prevX, double prevY) {
        if (super.mouseDragged(mouseX, mouseY, button, prevX, prevY)) return true;
        if (isDragged()) {
            setScroll(scrollClick(mouseY), false, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!super.mouseScrolled(mouseX, mouseY, delta))
            setScroll(Mth.clamp(scroll + (delta < 0 ? step : -step), min, max), false, true);
        return true;
    }

    public int getLastDelta() { return scroll - prev; }
    public int getScroll() { return scroll; }
}
*/