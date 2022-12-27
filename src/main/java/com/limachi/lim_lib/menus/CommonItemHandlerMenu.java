package com.limachi.lim_lib.menus;

import com.limachi.lim_lib.Log;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class CommonItemHandlerMenu extends CommonContainerMenu {
    protected IItemHandlerModifiable handler = null;

    protected CommonItemHandlerMenu(@Nullable MenuType<?> type, int id) { super(type, id); }

    protected CommonItemHandlerMenu(@Nullable MenuType<?> type, int id, IItemHandlerModifiable container) {
        super(type, id);
        handler = container;
    }
    protected CommonItemHandlerMenu(@Nullable MenuType<?> type, int id, Inventory playerInventory) {
        super(type, id);
        this.playerInventory = playerInventory;
    }

    protected CommonItemHandlerMenu(@Nullable MenuType<?> type, int id, Inventory playerInventory, IItemHandlerModifiable container) {
        super(type, id);
        handler = container;
        this.playerInventory = playerInventory;
    }

    @Override
    public void containerSlots(int y) { containerSlots(DEFAULT_LEFT_MARGIN, y, false, 9, 3); }

    @Override
    public void containerSlots(int y, boolean withScrollBar, int columns) {
        containerSlots(DEFAULT_LEFT_MARGIN, y, withScrollBar, columns, 3);
    }

    @Override
    public void containerSlots(int x, int y, boolean withScrollBar, int columns, int rows) {
        if (container != null) {
            newSlotSection();
            columns = Integer.min(9 - (withScrollBar ? 1 : 0), columns);
            rows = Integer.min(6, rows);
            for (int row = 0; row < rows; ++row)
                for (int column = 0; column < columns; ++column)
                    addSlot(new SlotItemHandler(handler, row * columns + column, x + column * DEFAULT_SLOT_WIDTH, y + row * DEFAULT_SLOT_HEIGHT));
        } else
            Log.error("Calling CommonItemHandlerMenu#containerSlots with null container!");
    }
}
