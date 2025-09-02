package com.limachi.lim_lib.tests;

import com.limachi.lim_lib.common.annotations.Config;
import com.limachi.lim_lib.common.annotations.RegisterBlock;
import com.limachi.lim_lib.common.annotations.RegisterBlockEntity;
import com.limachi.lim_lib.common.annotations.RegisterBlockItem;

import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TestBlock extends Block implements EntityBlock {

    @Config(min = "0", max = "15")
    public static int BLOCK_POWER = 7;

    @RegisterBlock("test_block")
    public static RegistrySupplier<Block> R_BLOCK;

    @RegisterBlockItem("test_block")
    public static RegistrySupplier<BlockItem> R_ITEM;

    public TestBlock() { super(Properties.ofFullCopy(Blocks.REDSTONE_BLOCK)); }

    public static class TestBlockEntity extends BlockEntity {

        @RegisterBlockEntity(blocks = "test_block")
        public static RegistrySupplier<BlockEntityType<BlockEntity>> TYPE;

        public TestBlockEntity(BlockPos blockPos, BlockState blockState) {
            super(TYPE.get(), blockPos, blockState);
        }
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return BLOCK_POWER;
    }

    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
        return BLOCK_POWER;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TestBlockEntity(blockPos, blockState);
    }
}
