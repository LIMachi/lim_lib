package com.limachi.lim_lib;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.item.ItemStack;

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
        out.setCount(Integer.max(Integer.min(count, stack.getMaxStackSize()), stack.getCount()));
        if (out.getCount() == stack.getCount()) return new Pair<>(out, ItemStack.EMPTY);
        ItemStack rem = stack.copy();
        rem.setCount(stack.getCount() - out.getCount());
        return new Pair<>(out, rem);
    }
}
