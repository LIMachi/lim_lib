package com.limachi.lim_lib.network;

import com.limachi.lim_lib.Default;
import net.minecraft.network.FriendlyByteBuf;

public interface IBufferSerializable extends Default {
    void readFromBuff(FriendlyByteBuf buff);
    void writeToBuff(FriendlyByteBuf buff);
}
