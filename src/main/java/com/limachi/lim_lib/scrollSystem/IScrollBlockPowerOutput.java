package com.limachi.lim_lib.scrollSystem;

import com.limachi.lim_lib.Maths;
import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.TextComponent; //VERSION 1.18.2
import net.minecraft.network.chat.Component; //VERSION 1.18.2
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * can be implemented on block / blockentity
 * do remember that blocks are singleton and not blockentity when using 'this'
 */
public interface IScrollBlockPowerOutput extends IScrollBlock {
    default void scroll(Level level, BlockPos pos, int delta, Player player) {
        BlockState state = level.getBlockState(pos);
        level.setBlock(pos, state.setValue(BlockStateProperties.POWER, Maths.clampModulus(state.getValue(BlockStateProperties.POWER) + delta, 1, 15)), 3);
    }

    default void scrollFeedBack(Level level, BlockPos pos, int delta, Player player) {
        player.displayClientMessage(
                //new TextComponent( //VERSION 1.18.2
                Component.literal( //VERSION 1.19.2
                        Integer.toString(Maths.clampModulus(level.getBlockState(pos).getValue(BlockStateProperties.POWER) + delta, 1, 15))), true);
    }
}
