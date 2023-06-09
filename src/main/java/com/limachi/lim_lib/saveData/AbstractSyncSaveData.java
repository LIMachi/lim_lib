package com.limachi.lim_lib.saveData;

import com.limachi.lim_lib.*;
import com.limachi.lim_lib.nbt.NBT;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.network.messages.SaveDataSyncMsg;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

public abstract class AbstractSyncSaveData extends SavedData {
    public final String name;
    public String level;
    public final SaveSync sync;
    private int lastMarkDirty = 0;
    private CompoundTag prevState;

    protected AbstractSyncSaveData(String name, SaveSync sync) {
        this.name = name;
        this.sync = sync;
    }

    protected AbstractSyncSaveData(String name) {
        this.name = name;
        this.sync = SaveSync.SERVER_TO_CLIENT;
    }

    public AbstractSyncSaveData setLevel(Level level) { this.level = World.asString(level); return this; }

    @Override
    public void setDirty() {
        super.setDirty();
        if (lastMarkDirty < Events.tick) {
            lastMarkDirty = Events.tick;
            if (Sides.isLogicalClient() && sync == SaveSync.BOTH_WAY)
                Events.delayedTask(1, ()-> NetworkManager.toServer(ModBase.COMMON_ID, pack(prevState == null)));
            else if (!Sides.isLogicalClient() && (sync == SaveSync.SERVER_TO_CLIENT || sync == SaveSync.BOTH_WAY))
                Events.delayedTask(1, ()-> NetworkManager.toClients(ModBase.COMMON_ID, pack(prevState == null)));
        }
    }

    public SaveDataSyncMsg pack(boolean send_all) {
        CompoundTag ser = save(new CompoundTag());
        SaveDataSyncMsg out;
        if (!send_all) {
            CompoundTag diff = NBT.extractDiff(ser, prevState);
            if (diff.isEmpty()) return null;
            if (diff.toString().length() < prevState.toString().length())
                out = new SaveDataSyncMsg(name, level, true, diff);
            else
                out = new SaveDataSyncMsg(name, level, false, ser);
        } else
            out = new SaveDataSyncMsg(name, level, false, ser);
        prevState = ser;
        return out;
    }

    @Override
    public abstract @Nonnull
    CompoundTag save(CompoundTag nbt);

    public abstract void load(CompoundTag nbt);

    public void applyDiff(CompoundTag nbt) {
        load(NBT.applyDiff(save(new CompoundTag()), nbt));
        setDirty(false);
    }
}
