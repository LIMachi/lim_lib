package com.limachi.lim_lib.messages;

import com.limachi.lim_lib.ModBase;
import com.limachi.lim_lib.Network;
import com.limachi.lim_lib.scrollSystem.IScrollItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

@Network.RegisterMessage(value = 2, modId = ModBase.COMMON_ID)
public class ScrolledItemMsg extends Network.Message {
    int slot;
    int delta;

    public ScrolledItemMsg(int slot, int delta) {
        this.slot = slot;
        this.delta = delta;
    }

    public ScrolledItemMsg(FriendlyByteBuf buffer) {
        slot = buffer.readInt();
        delta = buffer.readInt();
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeInt(delta);
    }

    /**
     * should only work client -> server
     */
    @Override
    public void serverWork(Player player) {
        Item item = player.getInventory().getItem(slot).getItem();
        if (item instanceof IScrollItem)
            ((IScrollItem)item).scroll(player, slot, delta);
    }
}
