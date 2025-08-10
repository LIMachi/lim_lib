package com.limachi.lim_lib.common.dataStorage;

import com.limachi.lim_lib.common.annotations.RegisterMsg;
import com.limachi.lim_lib.common.network.IS2CMsg;
import dev.architectury.networking.NetworkManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

@RegisterMsg
public record SyncDataFileMsg(String file, ResourceLocation level, CompoundTag data) implements IS2CMsg<SyncDataFileMsg> {
    @Override
    public void run(NetworkManager.PacketContext ctx) {
        var m = LevelDataFile.files.get(file);
        if (m != null && m.get(level) instanceof LevelDataFile ldf)
            ldf.load(data, null);
    }
}
