package com.limachi.lim_lib.data;

import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.Box2d;
import com.limachi.lim_lib.maths.IVec2i;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * an object that implement IAreaUser MUST have a final Area initialized and returned by the method 'areaHandler()'
 */
public interface IAreaUser {
    class Area {
        public record Animation(AnchoredBox origin, AnchoredBox target, double frames, Runnable onFinishedAnimation){}
        private Animation animation = null;
        private double frame = 0;
        public AnchoredBox box;
        private IVec2i defaultAnchor = new IVec2i(0, 0);
        private Box2d currentArea = new Box2d(0, 0);
        private final Supplier<Area> parentGetter;
        private boolean clamping;

        public Area(AnchoredBox box, @Nonnull Supplier<Area> parentGetter) {
            this(box, parentGetter, false);
        }

        public Area(AnchoredBox box, @Nonnull Supplier<Area> parentGetter, boolean clamping) {
            this.box = box.copy();
            this.parentGetter = parentGetter;
            this.clamping = clamping;
        }

        public void onlyRootShouldUseThis(AbstractContainerScreen<?> screen) {
            box = AnchoredBox.topLeftDeltaBox(screen.getGuiLeft(), screen.getGuiTop(), screen.getXSize(), screen.getYSize());
            defaultAnchor = new IVec2i(screen.getGuiLeft(), screen.getGuiTop());
        }

        public void setClamping(boolean state) { clamping = state; }
    }

    Area areaHandler();

    default boolean animateArea(AnchoredBox target, int frames, Runnable onFinishedAnimation) {
        Area handler = areaHandler();
        if (handler.animation == null) {
            handler.animation = new Area.Animation(handler.box.copy(), target, frames, onFinishedAnimation);
            handler.frame = 0;
            return true;
        }
        return false;
    }

    default boolean animateArea(AnchoredBox target, int frames) {
        return animateArea(target, frames, null);
    }

    default boolean isAreaAnimated() { return areaHandler().animation != null; }

    default Box2d currentArea() { return areaHandler().currentArea; }

    default boolean updateArea() {
        Area handler = areaHandler();
        if (handler.animation != null) {
            lerp(handler);
            return true;
        }
        Box2d t = getArea(handler, handler.box);
        if (t.equals(handler.currentArea))
            return false;
        handler.currentArea = t;
        return true;
    }

    private void lerp(Area handler) {
        if (handler.animation != null) {
            double f = handler.frame / handler.animation.frames();
            Box2d ob = getArea(handler, handler.animation.origin());
            Box2d tb = getArea(handler, handler.animation.target());
            int x = (int) Mth.lerp(f, ob.getX1(), tb.getX1());
            int y = (int)Mth.lerp(f, ob.getY1(), tb.getY1());
            int w = (int)Mth.lerp(f, ob.getWidth(), tb.getWidth());
            int h = (int)Mth.lerp(f, ob.getHeight(), tb.getHeight());
            handler.currentArea = new Box2d(x, y, w, h);
            handler.box = handler.box.copy().resize(w, h);
        }
    }

    private Box2d getArea(Area handler, AnchoredBox box) {
        Area parent = handler.parentGetter.get();
        if (parent != null) {
            IVec2i d = box.delta(parent.box.area());
            Box2d out = new Box2d(parent.currentArea.getX1() + d.x(), parent.currentArea.getY1() + d.y(), box.width(), box.height());
            if (parent.clamping)
                out.mergeCut(parent.currentArea);
            return out;
        }
        return new Box2d(handler.defaultAnchor.x(), handler.defaultAnchor.y(), box.width(), box.height());
    }

    default void partialTick(float partialTick) {
        Area handler = areaHandler();
        updateArea();
        if (handler.animation != null) {
            if ((handler.frame += partialTick) > handler.animation.frames) {
                handler.box = handler.animation.target;
                handler.animation.onFinishedAnimation.run();
                handler.animation = null;
                handler.frame = 0;
            }
        }
    }
}
