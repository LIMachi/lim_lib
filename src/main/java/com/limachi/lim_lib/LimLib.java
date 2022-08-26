package com.limachi.lim_lib;

import com.limachi.lim_lib.registries.Registries;
import com.limachi.lim_lib.registries.client.ClientRegistries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModBase.COMMON_ID)
public class LimLib extends ModBase {
    public LimLib() {
        super(ModBase.COMMON_ID, "LimLib");
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.register(Registries.class);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->()->{ bus.register(ClientRegistries.class); return true; });
    }
}
