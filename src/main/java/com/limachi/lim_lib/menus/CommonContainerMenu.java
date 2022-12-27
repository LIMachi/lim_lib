package com.limachi.lim_lib.menus;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.StackUtils;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@SuppressWarnings("unused")
public abstract class CommonContainerMenu extends AbstractContainerMenu {
    protected Container container = null;
    protected Inventory playerInventory = null;
    protected final ArrayList<Integer> slotSeparators = new ArrayList<>();
    public static final int DEFAULT_LEFT_MARGIN = 8;
    public static final int DEFAULT_SLOT_WIDTH = 18;
    public static final int DEFAULT_SLOT_HEIGHT = 18;
    public static final int DEFAULT_PLAYER_ROWS = 3;
    public static final int DEFAULT_PLAYER_COLUMNS = 9;

    protected CommonContainerMenu(@Nullable MenuType<?> type, int id) { super(type, id); }

    protected CommonContainerMenu(@Nullable MenuType<?> type, int id, Container container) {
        super(type, id);
        this.container = container;
    }
    protected CommonContainerMenu(@Nullable MenuType<?> type, int id, Inventory playerInventory) {
        super(type, id);
        this.playerInventory = playerInventory;
    }

    protected CommonContainerMenu(@Nullable MenuType<?> type, int id, Inventory playerInventory, Container container) {
        super(type, id);
        this.container = container;
        this.playerInventory = playerInventory;
    }

    public void playerSlots(int y) { playerSlots(DEFAULT_LEFT_MARGIN, y, y + 58); }

    public void playerSlots(int x, int y_inv, int y_belt) {
        if (playerInventory != null) {
            newSlotSection();
            for (int row = 0; row < DEFAULT_PLAYER_ROWS; ++row)
                for (int column = 0; column < DEFAULT_PLAYER_COLUMNS; ++column)
                    addSlot(new Slot(playerInventory,
                            DEFAULT_PLAYER_COLUMNS + row * DEFAULT_PLAYER_COLUMNS + column,
                            x + column * DEFAULT_SLOT_WIDTH,
                            y_inv + row * DEFAULT_SLOT_HEIGHT));
            for (int column = 0; column < DEFAULT_PLAYER_COLUMNS; ++column)
                addSlot(new Slot(playerInventory, column, x + column * DEFAULT_SLOT_WIDTH, y_belt));
        } else
            Log.error("Calling CommonContainerMenu#playerSlots with null playerInventory!");
    }

    public void containerSlots(int y) { containerSlots(DEFAULT_LEFT_MARGIN, y, false, 9, 3); }

    public void containerSlots(int y, boolean withScrollBar, int columns) {
        containerSlots(DEFAULT_LEFT_MARGIN, y, withScrollBar, columns, 3);
    }

    public void containerSlots(int x, int y, boolean withScrollBar, int columns, int rows) {
        if (container != null) {
            newSlotSection();
            columns = Integer.min(9 - (withScrollBar ? 1 : 0), columns);
            rows = Integer.min(6, rows);
            for (int row = 0; row < rows; ++row)
                for (int column = 0; column < columns; ++column)
                    addSlot(new Slot(container, row * columns + column, x + column * DEFAULT_SLOT_WIDTH, y + row * DEFAULT_SLOT_HEIGHT));
        } else
            Log.error("Calling CommonContainerMenu#containerSlots with null container!");
    }

    /**
     * Separator to differentiate groups of slots. By default, player slots and container slots should be separated.
     * PlayerSlots and containerSlots automatically call this function, only call this for groups of slots manually added.
     * This is only used for shift click behavior.
     */
    public void newSlotSection() { if (slots.size() > 0) slotSeparators.add(slots.size()); }

    /**
     * Player shift clicked the given slot, we try to merge/move the stack to the next section and return the remainder.
     * To prevent invalid infinite loops with the calling function, we ALWAYS return ItemStack.EMPTY.
     */
    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int slot) {
        if (slot < 0 || slot >= slots.size() || !slots.get(slot).hasItem()) return ItemStack.EMPTY;
        int sectionStart = 0;
        ItemStack tmp = slots.get(slot).getItem();
        for (int i = 0; i < slotSeparators.size() + 1; ++i) {
            int sectionFinish = i < slotSeparators.size() ? slotSeparators.get(i) : slots.size();
            if (sectionStart > slot || slot >= sectionFinish)
                if ((tmp = mergeStack(tmp, slot, sectionStart, sectionFinish, !(slots.get(sectionStart).container instanceof Inventory))).isEmpty()) break;
            sectionStart = sectionFinish;
        }
        slots.get(slot).set(tmp);
        return ItemStack.EMPTY;
    }

    protected ItemStack mergeStack(ItemStack stack, int blacklistedSlot, int sectionStart, int sectionFinish, boolean ascending) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        for (int pass = 0; pass < 2; ++pass)
            for (int i = ascending ? sectionStart : sectionFinish - 1; ascending ? i < sectionFinish : i >= sectionStart; i += ascending ? 1 : -1)
                if (i != blacklistedSlot) {
                    if (stack.isEmpty()) return ItemStack.EMPTY;
                    ItemStack target = slots.get(i).getItem();
                    if (pass == 0 ? StackUtils.canMergeNotEmpty(stack, target) : StackUtils.canMerge(stack, target)) {
                        Pair<ItemStack, ItemStack> p = StackUtils.merge(stack, target);
                        slots.get(i).set(p.getFirst());
                        stack = p.getSecond();
                    }
                }
        return stack;
    }

    public NonNullList<Slot> getSlots() { return slots; }
}
