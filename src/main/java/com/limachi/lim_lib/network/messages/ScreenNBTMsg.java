package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.menus.IAcceptDownStreamNBT;
import com.limachi.lim_lib.menus.IAcceptUpStreamNBT;
import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.network.RegisterMsg;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@RegisterMsg(7)
public record ScreenNBTMsg(int sid, int mid, Tag tag) implements IRecordMsg {

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
    public static void send(int mid, Tag tag) {
        Player player = Minecraft.getInstance().player;
        if (player != null)
            NetworkManager.toServer(LimLib.COMMON_ID, new ScreenNBTMsg(player.containerMenu.containerId, mid, tag));
    }

    public static void send(Player player, int mid, Tag tag) {
        if (player instanceof ServerPlayer p)
            NetworkManager.toClient(LimLib.COMMON_ID, p, new ScreenNBTMsg(player.containerMenu.containerId, mid, tag));
    }
}
