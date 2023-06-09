package com.limachi.lim_lib.blockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITickingBlockEntity {
    static void tick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (blockEntity instanceof ITickingBlockEntity tickingBlockEntity)
            tickingBlockEntity.tick(level, pos, state);
    }

    void tick(Level level, BlockPos pos, BlockState state);
}
