package com.limachi.lim_lib.integration.theOneProbePlugin;

import com.limachi.lim_lib.LimLib;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
/*
@Mod.EventBusSubscriber(modid = LimLib.COMMON_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TOPPlugin {

    private static boolean isLoaded = false;

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) { isLoaded = ModList.get().isLoaded("theoneprobe"); }

    @SubscribeEvent
    public static void onIMC(InterModEnqueueEvent event) {
        if (isLoaded)
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPIMC::new);
    }
}
*/