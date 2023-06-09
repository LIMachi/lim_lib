package com.limachi.lim_lib.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAutoNBTSerializable extends INBTSerializable<CompoundTag> {
    default CompoundTag serializeNBT() {
        return NBT.serializeAnnotations(this.getClass(), this, new CompoundTag());
    }

    default void deserializeNBT(CompoundTag nbt) {
        NBT.deserializeAnnotations(this.getClass(), this, nbt);
    }
}
