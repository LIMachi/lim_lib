package com.limachi.lim_lib.menus.slots;

import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BigSlotSA extends SlotAccessSlot implements IBigSlot {
    protected final Supplier<Integer> maxSizeInStacks;

    public BigSlotSA(SlotAccess sa, Supplier<Integer> maxSizeInStacks, int xPosition, int yPosition) {
        super(sa, xPosition, yPosition);
        this.maxSizeInStacks = maxSizeInStacks;
    }

    @Override
    public int getMaxStackSize() {
        int stacks = maxSizeInStacks.get();
        if (stacks > 0 && stacks * 64 <= 0)
            return Integer.MAX_VALUE;
        return stacks * 64;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        int stacks = maxSizeInStacks.get();
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

    @Override
    public int maxSizeInStacks() {
        return maxSizeInStacks.get();
    }
}
