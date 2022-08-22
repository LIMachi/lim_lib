package com.limachi.lim_lib;

import net.minecraft.network.FriendlyByteBuf;

public interface IPacketSerializable {
    void readFromBuff(FriendlyByteBuf buff);
    void writeToBuff(FriendlyByteBuf buff);
}
