package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import com.limachi.lim_lib.render.BlockRenderUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

@RegisterMsg
public record QueueOverlayRendererMsg(String level, BlockPos pos, int color, int ticks) implements IRecordMsg {
    @Override
    public void clientWork(Player player) {
        BlockRenderUtils.queueOverlayRenderer(level, pos, color, ticks);
    }
}
