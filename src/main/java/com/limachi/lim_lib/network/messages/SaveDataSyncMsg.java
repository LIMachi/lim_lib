package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import com.limachi.lim_lib.saveData.SaveDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

@RegisterMsg
public record SaveDataSyncMsg(String name, String level, boolean isDiff, CompoundTag nbt) implements IRecordMsg {
    public void clientWork(Player player) { SaveDataManager.clientUpdate(name, level, nbt, isDiff); }
    public void serverWork(Player player) { SaveDataManager.serverUpdate(player, name, level, nbt, isDiff); }
}