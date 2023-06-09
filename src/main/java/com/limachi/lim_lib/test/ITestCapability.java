package com.limachi.lim_lib.test;

import com.limachi.lim_lib.capabilities.ICopyCapOnDeath;
import com.limachi.lim_lib.nbt.IAutoNBTSerializable;
import com.limachi.lim_lib.registries.annotations.RegisterCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.CapabilityToken;

public interface ITestCapability extends IAutoNBTSerializable, ICopyCapOnDeath<ITestCapability> {

    @RegisterCapability(targets = {Player.class}, skip = "com.limachi.lim_lib.LimLib:useTests", cap = TestCapabilityImpl.class)
    CapabilityToken<ITestCapability> TOKEN = new CapabilityToken<>(){};

    int getCounter();
    void setCounter(int value);
}
