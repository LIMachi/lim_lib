package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import com.limachi.lim_lib.scrollSystem.IScrollBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

@RegisterMsg
public record ScrolledBlockMsg(BlockPos pos, int delta) implements IRecordMsg {
    public void serverWork(Player player) {
        BlockEntity be = player.level().getBlockEntity(pos);
        if (be instanceof IScrollBlock)
            ((IScrollBlock)be).scroll(player.level(), pos, delta, player);
        Block block = player.level().getBlockState(pos).getBlock();
        if (block instanceof IScrollBlock)
            ((IScrollBlock)block).scroll(player.level(), pos, delta, player);
    }
}