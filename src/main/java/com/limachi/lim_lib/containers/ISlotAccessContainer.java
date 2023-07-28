package com.limachi.lim_lib.containers;

import com.limachi.lim_lib.StackUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

/**
 * Thanks to this default interface, you can quickly create a container handler by only declaring as few methods as possible.
 */
public interface ISlotAccessContainer extends Container, IItemHandlerModifiable, StackedContentsCompatible {
    SlotAccess getSlotAccess(int slot);

    @Override
    default int getContainerSize() { return getSlots(); }

    default <T> T runOnAllSlots(BiFunction<Integer, SlotAccess, T> run, T def, boolean stopOnFirstNonDef) {
        for (int i = 0; i < getSlots(); ++i) {
            T out = run.apply(i, getSlotAccess(i));
            if (stopOnFirstNonDef && out != def)
                return out;
        }
        return def;
    }

    @Override
    default boolean isEmpty() { return runOnAllSlots((i, a)->a.get().isEmpty(), true, true); }

    @Override
    @Nonnull
    default ItemStack getItem(int slot) { return getSlotAccess(slot).get(); }

    @Override
    @Nonnull
    default ItemStack removeItem(int slot, int qty) {
        SlotAccess sa = getSlotAccess(slot);
        ItemStack ss = sa.get();
        qty = Integer.min(Integer.min(ss.getCount(), qty), ss.getItem().getMaxStackSize(ss));
        if (qty <= 0) return ItemStack.EMPTY;
        ItemStack out = ss.split(qty);
        sa.set(ss);
        setChanged();
        return out;
    }

    @Override
    @Nonnull
    default ItemStack removeItemNoUpdate(int slot) {
        SlotAccess sa = getSlotAccess(slot);
        ItemStack out = sa.get();
        sa.set(ItemStack.EMPTY);
        return out;
    }

    @Override
    default void setItem(int slot, @Nonnull ItemStack stack) {
        getSlotAccess(slot).set(stack);
        setChanged();
    }

    @Override
    default void setChanged() {
        if (this instanceof IContainerListenerHandler lh) {
            for (ContainerListener listener : lh.listeners())
                listener.containerChanged(this);
        }
    }

    @Override
    default boolean stillValid(@Nonnull Player player) { return true; }

    @Override
    default void clearContent() { runOnAllSlots((i, a)->a.set(ItemStack.EMPTY), true, false); }

    @Override
    default void setStackInSlot(int slot, @Nonnull ItemStack stack) { setItem(slot, stack); }

    @Override
    @Nonnull
    default ItemStack getStackInSlot(int slot) { return getItem(slot); }

    @Override
    @Nonnull
    default ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        ItemStack s = getItem(slot);
        if (!StackUtils.canMerge(stack, s)) return ItemStack.EMPTY;
        Pair<ItemStack, ItemStack> m = StackUtils.merge(stack, s, getSlotLimit(slot));
        if (!simulate)
            setItem(slot, m.getFirst());
        return m.getSecond();
    }

    @Override
    @Nonnull
    default ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0 || slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        Pair<ItemStack, ItemStack> m = StackUtils.extract(getStackInSlot(slot), amount);
        if (!simulate)
            setItem(slot, m.getSecond());
        return m.getFirst();
    }

    @Override
    default int getSlotLimit(int slot) { return getMaxStackSize(); }

    @Override
    default boolean canPlaceItem(int slot, @Nonnull ItemStack stack) { return isItemValid(slot, stack); }

    @Override
    default void fillStackedContents(@Nonnull StackedContents stacked) {
        for (int i = 0; i < getContainerSize(); ++i)
            stacked.accountStack(getItem(i));
    }

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

    default void fromTag(Tag tag) {
        if (tag instanceof ListTag list && list.getElementType() == Tag.TAG_COMPOUND) {
            clearContent();
            for (Tag entry : list)
                setItem(((CompoundTag)entry).getInt("Slot"), ItemStack.of((CompoundTag)entry));
        }
    }
}
