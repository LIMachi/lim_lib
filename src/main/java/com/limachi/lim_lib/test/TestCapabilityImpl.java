package com.limachi.lim_lib.test;

import com.limachi.lim_lib.nbt.TagSerialize;

public class TestCapabilityImpl implements ITestCapability {
    @CopyOnDeath
    @TagSerialize
    protected int counter = 0;

    @Override
    public int getCounter() {
        return counter;
    }

    @Override
    public void setCounter(int value) {
        counter = value;
    }
}
