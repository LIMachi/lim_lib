package com.limachi.lim_lib.containers;

import net.minecraft.world.inventory.Slot;

@SuppressWarnings("unused")
public interface ISlotProvider {
    Slot createSlot(int index, int x, int y);
}
