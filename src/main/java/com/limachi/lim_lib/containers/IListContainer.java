package com.limachi.lim_lib.containers;

import com.limachi.lim_lib.NBT;
import com.limachi.lim_lib.StackUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Thanks to this default interface, you can quickly create a list style container handler by only declaring a method
 * `stacks()`. By doing so, all methods of Container, IItemHandlerModifiable, StackedContentsCompatible, IAsTag
 * and IFromTag will be generated. If self also implements IContainerListenerHandler, setChanged will propagate
 * changes to listeners automatically. `stacks()` is expected to be a direct return of a final non static field.
 */
public interface IListContainer extends Container, IItemHandlerModifiable, StackedContentsCompatible, NBT.IAsTag, NBT.IFromTag {
    List<ItemStack> stacks();

    @Override default int getContainerSize() { return stacks().size(); }
    @Override default boolean isEmpty() { return stacks().stream().allMatch(ItemStack::isEmpty); }
    @Override default @Nonnull ItemStack getItem(int slot) { return slot >= 0 && slot < stacks().size() ? stacks().get(slot) : ItemStack.EMPTY; }

    @Override default @Nonnull ItemStack removeItem(int slot, int qty) {
        if (slot < 0 || slot >= stacks().size() || qty <= 0) return ItemStack.EMPTY;
        ItemStack ss = stacks().get(slot);
        qty = Integer.min(Integer.min(ss.getCount(), qty), ss.getItem()
//                .getItemStackLimit( //VERSION 1.18.2
                .getMaxStackSize( //VERSION 1.19.2
                        ss));
        if (qty <= 0) return ItemStack.EMPTY;
        ItemStack out = ss.split(qty);
        stacks().set(slot, ss);
        setChanged();
        return out;
    }

    @Override default @Nonnull ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= stacks().size()) return ItemStack.EMPTY;
        ItemStack out = stacks().get(slot);
        stacks().set(slot, ItemStack.EMPTY);
        return out;
    }

    @Override default void setItem(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= stacks().size() || stacks().get(slot).equals(stack, false)) return;
        stacks().set(slot, stack);
        setChanged();
    }

    @Override default void setChanged() {
        if (this instanceof IContainerListenerHandler lh) {
            for (ContainerListener listener : lh.listeners())
                listener.containerChanged(this);
        }
    }

    @Override default boolean stillValid(@Nonnull Player player) { return true; }
    @Override default void clearContent() { stacks().replaceAll(s->ItemStack.EMPTY); }
    @Override default void setStackInSlot(int slot, @Nonnull ItemStack stack) { setItem(slot, stack); }
    @Override default int getSlots() { return getContainerSize(); }
    @Override default @Nonnull ItemStack getStackInSlot(int slot) { return getItem(slot); }

    @Override default @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        ItemStack s = getItem(slot);
        if (!StackUtils.canMerge(stack, s)) return ItemStack.EMPTY;
        Pair<ItemStack, ItemStack> m = StackUtils.merge(stack, s, getSlotLimit(slot));
        if (!simulate)
            setItem(slot, m.getFirst());
        return m.getSecond();
    }

    @Override default @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0 || slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        Pair<ItemStack, ItemStack> m = StackUtils.extract(stacks().get(slot), amount);
        if (!simulate)
            setItem(slot, m.getSecond());
        return m.getFirst();
    }

    @Override default int getSlotLimit(int slot) { return getMaxStackSize(); }
    @Override default boolean isItemValid(int slot, @Nonnull ItemStack stack) { return canPlaceItem(slot, stack); }

    @Override default void fillStackedContents(@Nonnull StackedContents stacked) {
        for (int i = 0; i < getContainerSize(); ++i)
            stacked.accountStack(getItem(i));
    }

    @Override
    default Tag asTag() {
        ListTag out = new ListTag();
        for (int i = 0; i < getContainerSize(); ++i) {
            ItemStack stack = getItem(i);
            if (stack.isEmpty()) continue;
            CompoundTag entry = stack.serializeNBT();
            entry.putInt("Slot", i);
            out.add(entry);
        }
        return out;
    }

    @Override
    default void fromTag(Tag tag) {
        if (tag instanceof ListTag list && list.getElementType() == Tag.TAG_COMPOUND) {
            clearContent();
            for (Tag entry : list)
                setItem(((CompoundTag)entry).getInt("Slot"), ItemStack.of((CompoundTag)entry));
        }
    }
}
