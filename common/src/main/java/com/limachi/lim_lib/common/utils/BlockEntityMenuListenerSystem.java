package com.limachi.lim_lib.common.utils;

import com.limachi.lim_lib.common.annotations.RegisterMsg;
import com.limachi.lim_lib.common.network.IC2SMsg;

import com.limachi.lim_lib.common.network.IS2CMsg;
import dev.architectury.networking.NetworkManager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class BlockEntityMenuListenerSystem {
    public interface BEPlayerListeners {
        Set<ServerPlayer> getPlayers();

        default void addPlayer(ServerPlayer player) { if (player != null) getPlayers().add(player); }
        default void removePlayer(ServerPlayer player) { if (player != null) getPlayers().remove(player); }
        default void send(IS2CMsg<?> msg) { msg.sendToClients(getPlayers().iterator()); }
    }

    @RegisterMsg
    public record ClosingListener(BlockPos pos) implements IC2SMsg<ClosingListener> {
        @Override
        public void run(NetworkManager.PacketContext packetContext) {
            if (packetContext.getPlayer() instanceof ServerPlayer player)
                if (player.level().getBlockEntity(pos) instanceof BEPlayerListeners listeners)
                    listeners.removePlayer(player);
        }
    }
}
