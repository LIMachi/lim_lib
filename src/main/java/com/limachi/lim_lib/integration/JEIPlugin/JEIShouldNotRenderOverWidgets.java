package com.limachi.lim_lib.integration.JEIPlugin;

/*
@SuppressWarnings("rawtypes")
public class JEIShouldNotRenderOverWidgets implements IGlobalGuiHandler {

    @Override
    @Nonnull
    public Collection<Rect2i> getGuiExtraAreas() {
        ArrayList<Rect2i> widgets = new ArrayList<>();
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null)
            screen.renderables.forEach(t->{
                if (t instanceof com.limachi.lim_lib.widgetsOld.BaseWidget w) {
                    Box2d b = w.getArea();
                    widgets.add(new Rect2i((int)b.getX1(), (int)b.getX2(), (int)b.getWidth(), (int)b.getHeight()));
                } else if (t instanceof BaseWidget<?> w)
                    w.gatherScreenUsage(widgets);
            });
        return widgets;
    }
}
*/