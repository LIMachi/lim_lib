package com.limachi.lim_lib.widgetsOld;

/*
public class ViewPortWidget extends BaseWidget<ViewPortWidget> {

    protected static class InnerViewPortWidget extends BaseWidget<InnerViewPortWidget> {
        protected InnerViewPortWidget(@Nonnull AnchoredBox box) {
            super(box, new WidgetOptions().canAnimate(true));
        }
    }

    protected final InnerViewPortWidget inner;

    protected ViewPortWidget(@Nonnull AnchoredBox box, IVec2i inner) {
        super(box, new WidgetOptions().scissorRender(true));
        areaHandler.setClamping(true);
        this.inner = new InnerViewPortWidget(AnchoredBox.centeredBox(inner.x(), inner.y()));
        addChild(this.inner);
    }

    @Override
    public BaseWidget<ViewPortWidget> addChild(BaseWidget<?> widget) {
        if (widget == inner)
            return super.addChild(widget);
        else {
            inner.addChild(widget);
            return this;
        }
    }

    @Override
    public boolean isAreaAnimated() { return inner.isAreaAnimated(); }

    @Override
    public boolean animateArea(AnchoredBox target, int frames, Runnable onFinishedAnimation) {
        return inner.animateArea(target, frames, onFinishedAnimation);
    }
}
*/