package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.menus.IAcceptDownStreamNBT;
import com.limachi.lim_lib.menus.IAcceptUpStreamNBT;
import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.network.RegisterMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@RegisterMsg
public record ScreenNBTMsg(int sid, int mid, CompoundTag tag) implements IRecordMsg {

    @Override
    public void serverWork(Player player) {
        if (player.containerMenu.containerId == sid && player.containerMenu instanceof IAcceptUpStreamNBT m)
            m.upstreamNBTMessage(mid, tag);
    }

    @Override
    public void clientWork(Player player) {
        if (player.containerMenu.containerId == sid && player.containerMenu instanceof IAcceptDownStreamNBT m)
            m.downstreamNBTMessage(mid, tag);
    }

    @OnlyIn(Dist.CLIENT)
    public static void send(int mid, CompoundTag tag) {
        Player player = Minecraft.getInstance().player;
        if (player != null)
            NetworkManager.toServer(LimLib.COMMON_ID, new ScreenNBTMsg(player.containerMenu.containerId, mid, tag));
    }

    public static void send(Player player, int mid, CompoundTag tag) {
        if (player instanceof ServerPlayer p)
            NetworkManager.toClient(LimLib.COMMON_ID, p, new ScreenNBTMsg(player.containerMenu.containerId, mid, tag));
    }
}
