package com.limachi.lim_lib.containers;

import com.limachi.lim_lib.NBT;
import com.limachi.lim_lib.StackUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

/**
 * Thanks to this default interface, you can quickly create a proxy container handler by only declaring a method
 * `proxy()`. By doing so, all methods of Container, IItemHandlerModifiable, StackedContentsCompatible  IAsTag and
 * IFromTag will be generated. `proxy()` is expected to be a direct return of a final non static field.
 */
public interface IProxyContainer extends Container, IItemHandlerModifiable, StackedContentsCompatible, NBT.IAsTag, NBT.IFromTag {
    Container proxy();

    @Override default int getContainerSize() { return proxy().getContainerSize(); }
    @Override default boolean isEmpty() { return proxy().isEmpty(); }
    @Override default @Nonnull ItemStack getItem(int slot) { return proxy().getItem(slot); }
    @Override default @Nonnull ItemStack removeItem(int slot, int qty) { return proxy().removeItem(slot, qty); }
    @Override default @Nonnull ItemStack removeItemNoUpdate(int slot) { return proxy().removeItemNoUpdate(slot); }
    @Override default void setItem(int slot, @Nonnull ItemStack stack) { proxy().setItem(slot, stack); }
    @Override default void setChanged() { proxy().setChanged(); }
    @Override default boolean stillValid(@Nonnull Player player) { return proxy().stillValid(player); }
    @Override default void clearContent() { proxy().clearContent(); }
    @Override default void setStackInSlot(int slot, @Nonnull ItemStack stack) { proxy().setItem(slot, stack); }
    @Override default int getSlots() { return proxy().getContainerSize(); }
    @Override default @Nonnull ItemStack getStackInSlot(int slot) { return getItem(slot); }
    @Override default int getMaxStackSize() { return proxy().getMaxStackSize(); }
    @Override default boolean canPlaceItem(int slot, @Nonnull ItemStack stack) { return proxy().canPlaceItem(slot, stack); }

    @Override default @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || slot < 0 || slot >= proxy().getContainerSize()) return ItemStack.EMPTY;
        ItemStack s = proxy().getItem(slot);
        if (!StackUtils.canMerge(stack, s)) return ItemStack.EMPTY;
        Pair<ItemStack, ItemStack> m = StackUtils.merge(stack, s, getSlotLimit(slot));
        if (!simulate)
            setItem(slot, m.getFirst());
        return m.getSecond();
    }

    @Override default @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0 || slot < 0 || slot >= proxy().getContainerSize()) return ItemStack.EMPTY;
        Pair<ItemStack, ItemStack> m = StackUtils.extract(proxy().getItem(slot), amount);
        if (!simulate)
            setItem(slot, m.getSecond());
        return m.getFirst();
    }

    @Override default int getSlotLimit(int slot) { return proxy().getMaxStackSize(); }
    @Override default boolean isItemValid(int slot, @Nonnull ItemStack stack) { return proxy().canPlaceItem(slot, stack); }

    @Override default void fillStackedContents(@Nonnull StackedContents stacked) {
        Container c = proxy();
        for (int i = 0; i < c.getContainerSize(); ++i)
            stacked.accountStack(c.getItem(i));
    }

    @Override
    default Tag asTag() {
        ListTag out = new ListTag();
        Container c = proxy();
        for (int i = 0; i < c.getContainerSize(); ++i) {
            ItemStack stack = c.getItem(i);
            if (stack.isEmpty()) continue;
            CompoundTag entry = stack.serializeNBT();
            entry.putInt("Slot", i);
            out.add(entry);
        }
        return out;
    }

    @Override
    default void fromTag(Tag tag) {
        Container c = proxy();
        if (tag instanceof ListTag list && list.getElementType() == Tag.TAG_COMPOUND) {
            c.clearContent();
            for (Tag entry : list)
                c.setItem(((CompoundTag)entry).getInt("Slot"), ItemStack.of((CompoundTag)entry));
        }
    }
}
