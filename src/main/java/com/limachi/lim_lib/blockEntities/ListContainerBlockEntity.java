package com.limachi.lim_lib.blockEntities;

import com.limachi.lim_lib.containers.IListContainer;
//import com.limachi.lim_lib.menus.AutoScaleMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * simple block entity implementation that behave like an item inventory
 */
@SuppressWarnings("unused")
public abstract class ListContainerBlockEntity extends BaseContainerBlockEntity implements IListContainer, IOnRemoveBlockListener, IOnUseBlockListener {
    protected abstract int initialContainerSize();

    private final ArrayList<ItemStack> inventory;

    @Override
    public List<ItemStack> stacks() { return inventory; }

    protected ListContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        int size = initialContainerSize();
        inventory = new ArrayList<>(size);
        for (int i = 0; i < size; ++i)
            inventory.add(i, ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", asTag());
    }

    @Override
    @Nullable
    protected AbstractContainerMenu createMenu(int id, @Nonnull Inventory playerInventory) {
        return null;
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        fromTag(tag.get("Items"));
    }

    @Override
    @Nonnull
    protected IItemHandler createUnSidedHandler() { return this; }

    @Override
    public void onRemove(BlockState block, Level level, BlockPos pos, BlockState state, boolean bool) {
        Containers.dropContents(level, pos, this);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
//        if (!level.isClientSide())
//            AutoScaleMenu.open(player, this, getDisplayName());
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
