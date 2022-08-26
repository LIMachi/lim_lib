package com.limachi.lim_lib.network.messages;

import com.limachi.lim_lib.NBT;
import com.limachi.lim_lib.network.IRecordMsg;
import com.limachi.lim_lib.network.RegisterMsg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

@RegisterMsg(5)
public record PlayerPersistentDataMsg(Actions action, CompoundTag nbt) implements IRecordMsg {
    public enum Actions {
        OVERRIDE,
        MERGE,
        REMOVE_ENTRIES
    }

    private void runAction(CompoundTag target) {
        switch (action) {
            case OVERRIDE: NBT.clear(target).merge(nbt); return;
            case MERGE: target.merge(nbt); return;
            case REMOVE_ENTRIES:
                for (String k : nbt.getAllKeys())
                    target.remove(k);
        }
    }

    @Override
    public void clientWork(Player player) { if (player != null) runAction(player.getPersistentData()); }
    @Override
    public void serverWork(Player player) { if (player != null) runAction(player.getPersistentData()); }
}