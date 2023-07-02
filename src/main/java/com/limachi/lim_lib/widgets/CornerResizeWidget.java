package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchorPoint;
import com.limachi.lim_lib.maths.AnchoredBox;
import org.jetbrains.annotations.NotNull;

/*
public class CornerResizeWidget<T extends CornerResizeWidget<T>> extends BaseWidget<T> {

    protected int dragIndex = -1;

    protected CornerResizeWidget(@NotNull AnchoredBox box, @NotNull WidgetOptions options) {
        super(box, options.catchMouseEvents(true).canTakeFocus(true).canAnimate(true));
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (isOvered && button == 0) {
            double xf = relativeMouseX() / currentArea().getWidth();
            double yf = relativeMouseY() / currentArea().getHeight();
            if (xf < 0.2 && yf < 0.2)
                dragIndex = 1;
            else if (xf > 0.8 && yf < 0.2)
                dragIndex = 2;
            else if (xf > 0.8 && yf > 0.8)
                dragIndex = 3;
            else if (xf < 0.2 && yf > 0.8)
                dragIndex = 4;
            else
                dragIndex = 0;
            return true;
        }
        dragIndex = -1;
        return false;
    }

    @Override
    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        dragIndex = -1;
        return false;
    }

    @Override
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) {
        if (dragIndex == 0) {
            animateArea(new AnchoredBox(parentAnchorPointUnderMouse(), (int)currentArea().getWidth(), (int)currentArea().getHeight(), AnchorPoint.CENTER), 1);
            return true;
        }
        if (dragIndex > 0 && dragIndex < 5) {
            double w = currentArea().getWidth();
            double h = currentArea().getHeight();
            double xf = relativeMouseX() / w;
            double yf = relativeMouseY() / h;

            if (dragIndex == 1 || dragIndex == 4) {
                if (xf < 0)
                    w += w * -xf;
                else if (xf < 1)
                    w = w * (1 - xf);
                else
                    w = 2;
            } else {
                if (xf > 1)
                    w += w * (xf - 1);
                else if (xf > 0)
                    w = w * xf;
                else
                    w = 2;
            }
            if (dragIndex == 1 || dragIndex == 2) {
                if (yf < 0)
                    h += h * -yf;
                else if (yf < 1)
                    h = h * (1 - yf);
                else
                    h = 2;
            } else {
                if (yf > 1)
                    h += h * (yf - 1);
                else if (yf > 0)
                    h = h * yf;
                else
                    h = 2;
            }

            AnchorPoint lp = switch (dragIndex) {
                case 1 -> AnchorPoint.TOP_LEFT;
                case 2 -> AnchorPoint.TOP_RIGHT;
                case 3 -> AnchorPoint.BOTTOM_RIGHT;
                case 4 -> AnchorPoint.BOTTOM_LEFT;
                default -> AnchorPoint.CENTER;
            };
            animateArea(new AnchoredBox(parentAnchorPointUnderMouse(), (int)w, (int)h, lp), 1);
            return true;
        }
        return false;
    }
}
*/