package com.limachi.lim_lib.menus;

import com.limachi.lim_lib.test.TestChest;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("unused")
public abstract class PositionBoundMenu extends CommonItemHandlerMenu {
    protected ContainerLevelAccess accessor;

    protected abstract IItemHandlerModifiable clientContainer(Inventory playerInventory);

    protected PositionBoundMenu(MenuType<?> type, int id, Inventory playerInventory, @Nullable IItemHandlerModifiable container, BlockPos pos) {
        super(type, id, playerInventory);
        accessor = ContainerLevelAccess.create(playerInventory.player.level(), pos);
        handler = container != null ? container : clientContainer(playerInventory);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return stillValid(accessor, player, TestChest.TestChestBlock.R_BLOCK.get());
    }
}
