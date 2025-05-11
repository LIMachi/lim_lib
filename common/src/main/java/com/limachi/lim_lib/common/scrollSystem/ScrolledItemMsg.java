package com.limachi.lim_lib.common.scrollSystem;

import com.limachi.lim_lib.common.network.IC2SMsg;
import com.limachi.lim_lib.common.annotations.RegisterMsg;

import dev.architectury.networking.NetworkManager;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

@RegisterMsg
public record ScrolledItemMsg(int slot, int delta) implements IC2SMsg<ScrolledItemMsg> {
    @Override
    public void run(NetworkManager.PacketContext ctx) {
        Player player = ctx.getPlayer();
        Item item = player.getInventory().getItem(slot).getItem();
        if (item instanceof IScrollItem i)
            i.scroll(player, slot, delta);
    }
}