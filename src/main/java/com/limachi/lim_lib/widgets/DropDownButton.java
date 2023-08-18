package com.limachi.lim_lib.widgets;

/*
public class DropDownButton extends BaseButtonWidget<DropDownButton> {

    protected final BaseWidget<?> content;
    protected boolean opened;
    protected AnchoredBox extended;
    protected AnchoredBox closed;

    private static int updateAndGetHeight(BaseWidget<?> content) {
        content.updateArea();
        return (int)content.currentArea().getHeight();
    }

    public DropDownButton(@Nonnull AnchoredBox closed, Component title, boolean opened, BaseWidget<?> content) {
        super(opened ? closed.copy().resize(closed.width(), closed.height() + Integer.max(0, updateAndGetHeight(content))) : closed, title, w->{});
        if (!content.areaHandler().box.isBottomBound())
            Log.error(this, "DropDownButton initialized with content that is not bottom bound (please use AnchoredBox#bottomLeftBox/AnchoredBox#bottomRightBox)");
        widgetOptions.canAnimate(true);
        this.content = content;
        this.extended = closed.copy().resize(closed.width(), closed.height() + Integer.max(0, (int)content.currentArea().getHeight()));
        this.closed = closed;
        this.opened = opened;
        content.widgetOptions.active(opened);
        addChild(content);
    }

    @Override
    protected void updatePressedState(boolean state, int button, boolean andUpdate) {
        super.updatePressedState(state, button, andUpdate);
        if (pressedState)
            animateArea(extended, 5, ()->content.widgetOptions.active(true));
        else
            animateArea(closed, 5, ()->content.widgetOptions.active(false));
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (isOvered)
            updatePressedState(!pressedState, button, true);
        return isOvered;
    }
}
*/