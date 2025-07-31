package com.limachi.lim_lib.fabric;

import com.limachi.lim_lib.Properties;
import com.limachi.lim_lib.common.annotations.Loader;
import com.limachi.lim_lib.common.modCreation.Loaders;

import com.limachi.lim_lib.fabric.annotations.FabricMod;

@Loader(Loaders.Fabric)
@FabricMod(Properties.mod_id)
public final class LimLibFabric extends FabricEntryPoint {
    @Override
    protected String commonRootPackage() { return Properties.mod_root; }
}
