package com.limachi.lim_lib.widgetsOld;

/*
public class TestRenderWidget extends CornerResizeWidget<TestRenderWidget> {

    ItemStack stack = new ItemStack(Items.ACACIA_PLANKS, 42);
    FluidStack fluid = new FluidStack(Fluids.LAVA, 42042);
    boolean item = true;

    public TestRenderWidget(int x, int y) {
        super(AnchoredBox.topLeftDeltaBox(x, y, 18, 18), new WidgetOptions());
        setBackground(new ResourceLocation("fail"), BackgroundWidget.CUTOUT);
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (super.onMouseClicked(mouseX, mouseY, button)) return true;
        if (isOvered && button == 1) {
            item = !item;
            return true;
        }
        return false;
    }

    @Override
    public void backRender(@NotNull PoseStack stack, double mouseX, double mouseY, float partialTick) {
        if (item)
            GUIRenderUtils.renderItem(this.stack, (int)currentArea().getX1() + 1, (int)currentArea().getY1() + 1, (int)currentArea().getWidth() - 2, (int)currentArea().getHeight() - 2, 0x88FFFFFF, true);
        else
            GUIRenderUtils.renderFluid(fluid, (int)currentArea().getX1() + 1, (int)currentArea().getY1() + 1, (int)currentArea().getWidth() - 2, (int)currentArea().getHeight() - 2, 0x88FFFFFF, true);
    }
}
*/