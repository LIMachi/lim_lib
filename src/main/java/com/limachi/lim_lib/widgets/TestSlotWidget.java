package com.limachi.lim_lib.widgets;

/*
public class TestSlotWidget extends BaseWidget<TestSlotWidget> {
    protected Slot slot;

    public TestSlotWidget(AnchoredBox box, Slot slot) {
        super(box.copy().resize(18, 18), new WidgetOptions().canTakeFocus(true).catchMouseEvents(true).canAnimate(true));
        this.slot = slot;
        setBackground(RenderUtils.SLOTS_TEXTURE, new Box2d(18, 18));
    }

    @Override
    public void frontRender(@NotNull PoseStack stack, double mouseX, double mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, backgroundTexture);
        RenderUtils.blitMiddleExp(null, stack, 0, currentArea().copy().move(10, 10), backgroundCutout);
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        return isOvered && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    }

    @Override
    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) {
        if (isDragged() && root.dragButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            animateArea(new AnchoredBox(new AnchorPoint(parent.relativeMouseX() / parent.currentArea().getWidth(), false, parent.relativeMouseY() / parent.currentArea().getHeight(), false, true), 18, 18, AnchorPoint.CENTER), 1);
            return true;
        }
        return false;
    }

    protected void updateSlotPos() {
        slot.x = (int)currentArea().getX1() - screen().getGuiLeft() + 1;
        slot.y = (int)currentArea().getY1() - screen().getGuiTop() + 1;
    }

//    @Override
//    public TestSlotWidget attachTo(BaseWidget<?> parent) {
//        TestSlotWidget out = super.attachTo(parent);
//        updateSlotPos();
//        if (screen.isSet() && screen().getMenu() instanceof WidgetContainerMenu m && !m.slots.contains(slot))
//            m.addSlot(slot);
//        return out;
//    }

    @Override
    protected void onAreaResize() { updateSlotPos(); }
}
*/