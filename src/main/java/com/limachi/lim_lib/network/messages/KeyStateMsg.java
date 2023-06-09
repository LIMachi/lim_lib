package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.KeyMapController;
import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import net.minecraft.world.entity.player.Player;

@RegisterMsg
public record KeyStateMsg(int key, boolean state) implements IRecordMsg {
    public void serverWork(Player player) { KeyMapController.syncKeyMapServer(player, key, state); }
    public void clientWork(Player player) { KeyMapController.KEY_BINDINGS.get(key).forceKeyState(player, state); }
}