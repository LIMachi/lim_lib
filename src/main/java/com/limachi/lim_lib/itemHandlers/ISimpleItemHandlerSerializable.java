package com.limachi.lim_lib.itemHandlers;

import com.limachi.lim_lib.network.IBufferSerializable;
import com.limachi.lim_lib.containers.ISlotProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

@SuppressWarnings("unused")
public interface ISimpleItemHandlerSerializable extends IItemHandlerModifiable, INBTSerializable<CompoundTag>, ISlotProvider, IBufferSerializable {
    default Slot createSlot(int index, int x, int y) { return new SlotItemHandler(this, index, x, y); }
}
