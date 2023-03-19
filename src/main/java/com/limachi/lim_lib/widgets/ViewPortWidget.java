package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.IVec2i;

import javax.annotation.Nonnull;

/*
public class ViewPortWidget extends BaseWidget<ViewPortWidget> {
    protected boolean top;

    public static ViewPortWidget create(@Nonnull AnchoredBox box, IVec2i innerArea) {
        return new ViewPortWidget(box, true).addChild(new ViewPortWidget(AnchoredBox.centeredBox(innerArea.x(), innerArea.y()), false));
    }

    protected ViewPortWidget(@Nonnull AnchoredBox box, boolean top) {
        super(box, top ? new WidgetOptions().canAnimate(true).catchMouseEvents(true).catchEventsFirst(true).scissorRender(true) : new WidgetOptions().canAnimate(true));
        this.top = top;
    }

    @Override
    public ViewPortWidget addChild(BaseWidget<?> widget) {
        if (top && children.size() == 0 && widget instanceof ViewPortWidget b && !b.top)
            return super.addChild(widget);
        if (children.size() != 1) {
            Log.error(widget, 1, "Could not insert child widget in viewport");
            return this;
        }
        children.get(0).addChild(widget);
        return this;
    }

    public boolean animateBottom(AnchoredBox target, int frames) {
        if (!top || children.size() != 1) {
            Log.error(this, 1, "Could not animate bottom view port");
            return false;
        }
        return children.get(0).animate(target, frames);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (top && !isMouseOver(mouseX, mouseY)) return;
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (top && !isMouseOver(mouseX, mouseY)) return false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (top && !isMouseOver(mouseX, mouseY)) return false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) {
        if (top && !isMouseOver(mouseX, mouseY)) return false;
        return super.mouseDragged(mouseX, mouseY, button, fromX, fromY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (top && !isMouseOver(mouseX, mouseY)) return false;
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
*/