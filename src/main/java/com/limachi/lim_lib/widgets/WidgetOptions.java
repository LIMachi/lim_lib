package com.limachi.lim_lib.widgets;

public class WidgetOptions {
    private boolean canAnimate = false; //set to true will allow calls to animation, allowing the resizing of the widget and positioning
    private boolean canTakeFocus = false; //can this widget automatically take focus on click (no matter the button, if overing)
    private boolean looseFocusOnClickOff = true;  //can this widget automatically loose focus on click (no matter the button, if not overing)
    private boolean shouldRender = true; //should this widget be visible. do not affect the childrens, use shouldRenderChildren instead
    private boolean shouldRenderChildren = true;
    private boolean catchMouseEvents = false; //should this widget react to mouse events (this includes overing callbacks), isOvered flag will still be updated though
    private boolean catchKeyboardEvents = false; //should this widget react to keyboard events
    private boolean scissorRender = false; //should this widget use GLFW scissor to prevent objects from rendering outside it's area (affects the children rendering).

    public WidgetOptions canAnimate(boolean state) { canAnimate = state; return this; }
    public WidgetOptions canTakeFocus(boolean state) { canTakeFocus = state; return this; }
    public WidgetOptions looseFocusOnClickOff(boolean state) { looseFocusOnClickOff = state; return this; }
    public WidgetOptions shouldRender(boolean state) { shouldRender = state; return this; }
    public WidgetOptions shouldRenderChildren(boolean state) { shouldRenderChildren = state; return this; }
    public WidgetOptions catchMouseEvents(boolean state) { catchMouseEvents = state; return this; }
    public WidgetOptions catchKeyboardEvents(boolean state) { catchKeyboardEvents = state; return this; }
    public WidgetOptions scissorRender(boolean state) { scissorRender = state; return this; }

    public boolean canAnimate() { return canAnimate; }
    public boolean canTakeFocus() { return canTakeFocus; }
    public boolean looseFocusOnClickOff() { return looseFocusOnClickOff; }
    public boolean shouldRender() { return shouldRender; }
    public boolean shouldRenderChildren() { return shouldRenderChildren; }
    public boolean catchMouseEvents() { return catchMouseEvents; }
    public boolean catchKeyboardEvents() { return catchKeyboardEvents; }
    public boolean scissorRender() { return scissorRender; }
}
