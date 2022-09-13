package com.limachi.lim_lib.blockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockEntity;

public interface IOnRemoveBlockListener extends IForgeBlockEntity {
    void onRemove(BlockState block, Level level, BlockPos pos, BlockState state, boolean bool);
}
