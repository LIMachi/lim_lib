package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import com.limachi.lim_lib.scrollSystem.IScrollItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

@RegisterMsg(2)
public record ScrolledItemMsg(int slot, int delta) implements IRecordMsg {
    public void serverWork(Player player) {
        Item item = player.getInventory().getItem(slot).getItem();
        if (item instanceof IScrollItem i)
            i.scroll(player, slot, delta);
    }
}