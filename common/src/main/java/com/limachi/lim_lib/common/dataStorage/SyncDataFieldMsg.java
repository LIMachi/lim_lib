package com.limachi.lim_lib.common.dataStorage;

import com.limachi.lim_lib.common.annotations.RegisterMsg;
import com.limachi.lim_lib.common.network.IS2CMsg;

import dev.architectury.networking.NetworkManager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

@RegisterMsg
public record SyncDataFieldMsg(ResourceLocation level, CompoundTag data) implements IS2CMsg<SyncDataFieldMsg> {
    @Override
    public void run(NetworkManager.PacketContext ctx) {
        for (String id : data.getAllKeys()) {
            if (DataField.fields.get(id) instanceof DataField<?> ldf)
                ldf.receiveSync(data.get(id), level);
        }
    }
}
