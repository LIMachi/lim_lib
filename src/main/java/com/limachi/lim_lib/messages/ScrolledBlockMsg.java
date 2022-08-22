package com.limachi.lim_lib.messages;

import com.limachi.lim_lib.ModBase;
import com.limachi.lim_lib.Network;
import com.limachi.lim_lib.scrollSystem.IScrollBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

@Network.RegisterMessage(value = 1, modId = ModBase.COMMON_ID)
public class ScrolledBlockMsg extends Network.Message {
    BlockPos pos;
    int delta;

    public ScrolledBlockMsg(BlockPos pos, int delta) {
        this.pos = pos;
        this.delta = delta;
    }

    public ScrolledBlockMsg(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        delta = buffer.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(delta);
    }

    /**
     * should only work client -> server
     */
    @Override
    public void serverWork(Player player) {
        BlockEntity be = player.level.getBlockEntity(pos);
        if (be instanceof IScrollBlock)
            ((IScrollBlock)be).scroll(player.level, pos, delta, player);
        Block block = player.level.getBlockState(pos).getBlock();
        if (block instanceof IScrollBlock)
            ((IScrollBlock)block).scroll(player.level, pos, delta, player);
    }
}
