package com.limachi.lim_lib.network;

import net.minecraft.world.entity.player.Player;

public interface IRecordMsg {
    default void clientWork(Player player) {}
    default void serverWork(Player player) {}
}
