package com.limachi.lim_lib.test;

import com.limachi.lim_lib.saveData.AbstractSyncSaveData;
import com.limachi.lim_lib.saveData.RegisterSaveData;
import com.limachi.lim_lib.saveData.SaveSync;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

@RegisterSaveData
public class TestData extends AbstractSyncSaveData {

    private int test;

    public TestData(String name, SaveSync sync) { super(name, sync); }

    public int getTest() { return test; }
    public void setTest(int test) { this.test = test; setDirty(); }

    @NotNull
    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("test", test);
        return nbt;
    }

    @Override
    public void load(CompoundTag nbt) {
        test = nbt.getInt("test");
    }
}
