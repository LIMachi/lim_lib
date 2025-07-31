package com.limachi.lim_lib.neoforge;

import com.limachi.lim_lib.Properties;
import com.limachi.lim_lib.common.annotations.Loader;
import com.limachi.lim_lib.common.modCreation.Loaders;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Loader(Loaders.NeoForge)
@Mod(Properties.mod_id)
public final class LimLibNeo extends NeoEntryPoint {
    public LimLibNeo(IEventBus modBus, Dist dist) { super(modBus, dist); }

    @Override
    protected String commonRootPackage() { return Properties.mod_root; }
}
