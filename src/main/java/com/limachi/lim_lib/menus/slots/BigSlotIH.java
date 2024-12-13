package com.limachi.lim_lib.menus.slots;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class BigSlotIH extends SlotItemHandler implements IBigSlot {

    public BigSlotIH(IItemHandlerModifiable handler, int index, int xPosition, int yPosition) {
        super(handler, index, xPosition, yPosition);
    }

    @Override
    public int maxSizeInStacks() {
        double local = getItemHandler().getStackInSlot(getContainerSlot()).getMaxStackSize(); //ex 16 for ender pearl
        double max = getItemHandler().getSlotLimit(getContainerSlot()); //ex 64 for 4 stacks of ender pearl
        return (int)Math.ceil(max / local);
    }

    @Override
    public int getMaxStackSize() {
        int stacks = maxSizeInStacks();
        if (stacks > 0 && stacks * 64 <= 0)
            return Integer.MAX_VALUE;
        return stacks * 64;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        int stacks = maxSizeInStacks();
        int max = stack.getMaxStackSize();
        if (stacks > 0 && max > 0 && stacks * max <= 0)
            return Integer.MAX_VALUE;
        return stacks * max;
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        ItemStack current = getItem();
        int max = Integer.min(Integer.min(amount, current.getCount()), current.getMaxStackSize());
        if (max > 0) {
            ItemStack extracted = current.copyWithCount(max);
            current.setCount(current.getCount() - max);
            return extracted;
        }
        return ItemStack.EMPTY;
    }
}
