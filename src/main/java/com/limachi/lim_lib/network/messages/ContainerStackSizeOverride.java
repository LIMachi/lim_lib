package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import net.minecraft.world.entity.player.Player;

/**
 * Message to send to clients to override the stack size in a slot (if it is above signed byte max, aka 127, otherwise the vanilla code is enough)
 */
@RegisterMsg
public record ContainerStackSizeOverride(int container, int slot, int size) implements IRecordMsg {
    public void clientWork(Player player) {
        if (player.containerMenu.containerId == container)
            player.containerMenu.slots.get(slot).getItem().setCount(size);
    }
}