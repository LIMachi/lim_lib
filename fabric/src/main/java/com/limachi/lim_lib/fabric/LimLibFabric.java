package com.limachi.lim_lib.fabric;

import com.limachi.lim_lib.common.annotations.Loader;
import com.limachi.lim_lib.common.modCreation.Loaders;

import com.limachi.lim_lib.fabric.annotations.FabricMod;

@Loader(Loaders.Fabric)
@FabricMod("lim_lib")
public final class LimLibFabric extends FabricEntryPoint {
    @Override
    protected String commonRootPackage() {
        return "com.limachi.lim_lib";
    }
}
