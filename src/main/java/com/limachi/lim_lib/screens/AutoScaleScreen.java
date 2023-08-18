package com.limachi.lim_lib.screens;

//import com.limachi.lim_lib.menus.AutoScaleMenu;

//@OnlyIn(Dist.CLIENT)
//@RegisterMenuScreen
//public class AutoScaleScreen extends WidgetContainerScreen/*AbstractWidgetContainerScreen*/<AutoScaleMenu> implements ILimLibScreen {
/*

    protected final ScrollBarWidget scrollBar = new ScrollBarWidget(152, 15, 110, 0, 0, s->reorganiseSlots());

    public AutoScaleScreen(AutoScaleMenu menu, Inventory playerInventory, Component title) { super(menu, playerInventory, title); }

    @Override
    protected void init() {
        imageHeight = menu.getRows() * 18 + 112;
        inventoryLabelY = imageHeight - 94;
        if (menu.validSlots() > 54) {
            scrollBar.setMax(menu.pages(), true);
            addRenderableWidget(scrollBar);
            scrollBar.attachToScreen(this);
        }
        super.init();
    }

    protected void reorganiseSlots() {
        int page = scrollBar.getScroll();
        List<Slot> slots = menu.getSlots();
        int i = 36;
        for (; i < slots.size() && i < 36 + 8 * page; ++i) {
            Slot slot = slots.get(i);
            slot.x = Integer.MIN_VALUE;
            slot.y = Integer.MIN_VALUE;
        }
        for (int row = 0; row < 6 && i < slots.size(); ++row)
            for (int column = 0; column < 8 && i < slots.size(); ++column) {
                Slot slot = slots.get(i++);
                slot.x = 8 + column * 18;
                slot.y = 17 + row * 18;
            }
        for (; i < slots.size(); ++i) {
            Slot slot = slots.get(i);
            slot.x = Integer.MIN_VALUE;
            slot.y = Integer.MIN_VALUE;
        }
    }

    protected int validSlots() {
        int t = menu.validSlots();
        if (t <= 54) return t;
        return t - scrollBar.getScroll() * 8;
    }

    @Override
    protected void renderBg(PoseStack stack, float tick, int mouseX, int mouseY) { //FIXME
        super.renderBg(stack, tick, mouseX, mouseY);
        RenderUtils.playerSlots(this, stack, 7, 29 + menu.getRows() * 18, true);
        RenderUtils.slots(this, stack, 7 + (menu.validSlots() <= 9 ? 3 * 18 : 0), 16, menu.getRows(), menu.getColumns(), validSlots());
    }

    @Override
    public boolean overrideTooltip(List<Component> tooltip) {
        if (tooltipOverride.size() > 0) return false;
        tooltipOverride.addAll(tooltip);
        return true;
    }
}
*/