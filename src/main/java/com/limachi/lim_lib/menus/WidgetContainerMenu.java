package com.limachi.lim_lib.menus;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WidgetContainerMenu extends AbstractContainerMenu {
    protected WidgetContainerMenu(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public Slot addSlot(Slot slot) {
        return super.addSlot(slot);
    }
}
