package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import com.limachi.lim_lib.saveData.SaveDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

@RegisterMsg(4)
public record SaveDataSyncMsg(String name, boolean isDiff, CompoundTag nbt) implements IRecordMsg {
    public void clientWork(Player player) { SaveDataManager.clientUpdate(name, nbt, isDiff); }
    public void serverWork(Player player) { SaveDataManager.serverUpdate(player, name, nbt, isDiff); }
}