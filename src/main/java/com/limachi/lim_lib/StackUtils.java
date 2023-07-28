package com.limachi.lim_lib;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.Predicate;

public class StackUtils {
    /**
     * test if two stacks can be merged, including empty stacks
     * @param s1 the first stack to test
     * @param s2 the second stack to test
     * @return true if the stacks can be merged in one
     */
    public static boolean canMerge(ItemStack s1, ItemStack s2) {
        return s1.isEmpty() || s2.isEmpty() || ItemStack.isSameItemSameTags(s1, s2);
    }

    public static boolean canMergeNotEmpty(ItemStack s1, ItemStack s2) {
        return !s1.isEmpty() && !s2.isEmpty() && ItemStack.isSameItemSameTags(s1, s2);
    }

    /**
     * merge two stacks together and return the merge and remainder in a pair, does not change s1 and s2
     * only call this function after a check with canMergeNoSizeCheck or canMerge
     * @param s1 first stack to be merged
     * @param s2 second stack to be merged
     * @return a pair where the first stack is the merge, and the second is the remainder or empty
     */
    public static Pair<ItemStack, ItemStack> merge(ItemStack s1, ItemStack s2) { return merge(s1, s2, -1); }

    /**
     * merge two stacks together and return the merge and remainder in a pair, does not change s1 and s2
     * only call this function after a check with canMergeNoSizeCheck or canMerge
     * @param s1 first stack to be merged
     * @param s2 second stack to be merged
     * @param max maximum amount that can be merged, set to -1 to ignore
     * @return a pair where the first stack is the merge, and the second is the remainder or empty
     */
    public static Pair<ItemStack, ItemStack> merge(ItemStack s1, ItemStack s2, int max) {
        max = Integer.min(max == -1 ? Integer.MAX_VALUE : max, s1.getMaxStackSize());
        int total = s1.getCount() + s2.getCount();
        ItemStack out = s1.isEmpty() ? s2.copy() : s1.copy();
        ItemStack rem;
        if (total <= max) {
            out.setCount(total);
            rem = ItemStack.EMPTY;
        } else {
            out.setCount(max);
            rem = s1.isEmpty() ? s2.copy() : s1.copy();
            rem.setCount(total - max);
        }
        return new Pair<>(out, rem);
    }

    public static Pair<ItemStack, ItemStack> extract(ItemStack stack, int count) {
        ItemStack out = stack.copy();
        out.setCount(Integer.min(Integer.min(count, stack.getMaxStackSize()), stack.getCount()));
        if (out.getCount() == stack.getCount()) return new Pair<>(out, ItemStack.EMPTY);
        ItemStack rem = stack.copy();
        rem.setCount(stack.getCount() - out.getCount());
        return new Pair<>(out, rem);
    }

    public static SlotAccess slotAccessForItemHandler(final IItemHandler inv, final int slot, final Predicate<ItemStack> pred) {
        return new SlotAccess() {
            @Override
            public ItemStack get() { return inv.getStackInSlot(slot); }

            @Override
            public boolean set(ItemStack stack) {
                if (!pred.test(stack) || !inv.isItemValid(slot, stack))
                    return false;
                if (inv instanceof IItemHandlerModifiable mod)
                    mod.setStackInSlot(slot, stack);
                else {
                    inv.extractItem(slot, inv.getStackInSlot(slot).getCount(), false);
                    inv.insertItem(slot, stack, false);
                }
                return true;
            }
        };
    }

    public static SlotAccess slotAccessForItemHandler(final IItemHandler inv, final int slot) {
        return slotAccessForItemHandler(inv, slot, s->true);
    }

    public static SlotAccess slotAccessForItemEntity(final ItemEntity entity, final Predicate<ItemStack> pred) {
        return new SlotAccess() {
            @Override
            public ItemStack get() { return entity.getItem(); }

            @Override
            public boolean set(ItemStack stack) {
                if (pred.test(stack)) {
                    if (stack.isEmpty()) {
                        entity.getItem().setCount(0);
                        entity.remove(Entity.RemovalReason.DISCARDED);
                        return true;
                    }
                    if (ItemStack.isSameItemSameTags(stack, entity.getItem())) {
                        entity.getItem().setCount(stack.getCount());
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static SlotAccess slotAccessForItemEntity(final ItemEntity entity) { return slotAccessForItemEntity(entity, s->true); }

    public static SlotAccess slotAccessForStack(final ItemStack stack, final Predicate<ItemStack> pred) {
        return new SlotAccess() {
            @Override
            public ItemStack get() { return stack; }

            @Override
            public boolean set(ItemStack in) {
                if (pred.test(in)) {
                    if (in.isEmpty()) {
                        stack.setCount(0);
                        return true;
                    }
                    if (ItemStack.isSameItemSameTags(in, stack)) {
                        stack.setCount(in.getCount());
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static SlotAccess slotAccessForStack(final ItemStack stack) { return slotAccessForStack(stack, s->true); }

    public static void mergeSlots(SlotAccess from, SlotAccess to, boolean doCheck) {
        if (doCheck) {}
        ItemStack ts = to.get();
        int room = ts.getMaxStackSize() - ts.getCount();
        if (room > 0) {
            ItemStack fs = from.get();
            int toInsert = Integer.min(Integer.min(room, fs.getCount()), fs.getMaxStackSize());
            if (toInsert > 0) {
                if (ts.isEmpty()) {
                    ts = fs.copy();
                    ts.setCount(toInsert);
                } else
                    ts.setCount(ts.getCount() + toInsert);
                fs.setCount(fs.getCount() - toInsert);
                from.set(fs);
                to.set(ts);
            }
        }
    }
}
