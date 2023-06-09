package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchorPoint;
import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.Box2d;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
/*
public class SlotAccessWidget extends BaseWidget<SlotAccessWidget> {

    private static ResourceLocation BG = null;
    protected final InnerSlot slot;

    protected static class InnerSlot extends Slot {

        private final SlotAccess sa;
        private final SlotAccessWidget parent;

        private static final Container EMPTY = new SimpleContainer(0);

        public InnerSlot(SlotAccess slot, int x, int y, SlotAccessWidget widget) {
            super(EMPTY, 0, x, y);
            sa = slot;
            parent = widget;
        }

        @Override
        public void onQuickCraft(ItemStack moved, ItemStack original) {
            int i = original.getCount() - moved.getCount();
            if (i > 0)
                onQuickCraft(original, i);
        }

        @Override
        public ItemStack getItem() { return sa.get(); }

        @Override
        public void set(ItemStack stack) { sa.set(stack); }

        @Override
        public void initialize(ItemStack stack) { sa.set(stack); }

        @Override
        public int getMaxStackSize() { return Container.LARGE_MAX_STACK_SIZE; }

        @Override
        public ItemStack remove(int quantity) {
            ItemStack t = sa.get().copy();
            ItemStack out = t.split(quantity);
            sa.set(t);
            return out;
        }

        @Override
        public boolean isActive() { return parent.widgetOptions.active(); }

        @Override
        public boolean isSameInventory(Slot other) {
            return other.getItem() == sa.get();
        }
    }

    public SlotAccessWidget(@Nonnull AnchorPoint anchor, @Nonnull SlotAccess slot, @Nonnull AbstractContainerScreen<?> screen) {
        this(anchor, AnchorPoint.TOP_LEFT, slot, screen);
    }
    public SlotAccessWidget(@Nonnull AnchorPoint parent, @Nonnull AnchorPoint local, @Nonnull SlotAccess slot, @Nonnull AbstractContainerScreen<?> screen) {
        super(new AnchoredBox(parent, 16, 16, local), new WidgetOptions().canTakeFocus(true).catchMouseEvents(true));
        this.slot = new InnerSlot(slot, 0, 0, this);
        screen.getMenu().slots.add(this.slot);
    }

    @Override
    public boolean updateArea() {
        boolean out = super.updateArea();
        Box2d area = currentArea();
        slot.x = (int)(area.getX1());
        slot.y = (int)(area.getY1());
        return out;
    }
}
*/