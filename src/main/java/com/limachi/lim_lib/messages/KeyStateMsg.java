package com.limachi.lim_lib.messages;

import com.limachi.lim_lib.KeyMapController;
import com.limachi.lim_lib.ModBase;
import com.limachi.lim_lib.Network;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

@Network.RegisterMessage(value = 3, modId = ModBase.COMMON_ID)
public class KeyStateMsg extends Network.Message {
    int key;
    boolean state;

    public KeyStateMsg(int key, boolean state) {
        this.key = key;
        this.state = state;
    }

    public KeyStateMsg(FriendlyByteBuf buffer) {
        key = buffer.readInt();
        state = buffer.readBoolean();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(key);
        buffer.writeBoolean(state);
    }

    @Override
    public void serverWork(Player player) { KeyMapController.syncKeyMapServer(player, key, state); }

    @Override
    public void clientWork(Player player) { KeyMapController.KEY_BINDINGS.get(key).forceKeyState(player, state); }
}
