package com.limachi.lim_lib;

import com.limachi.lim_lib.integration.Curios.CuriosIntegration;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.registries.Stage;
import com.limachi.lim_lib.registries.ClientRegistries;
import com.limachi.lim_lib.registries.Registries;
import com.limachi.lim_lib.registries.StaticInitializer;
import com.limachi.lim_lib.saveData.SaveDataManager;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModBase {

    public static final String COMMON_ID = "lim_lib";

    public static final HashMap<String, ModBase> INSTANCES = new HashMap<>();
    protected CreativeModeTab tab = CreativeModeTab.TAB_MISC;

    public ModBase(@Nonnull String modId, @Nonnull String name, @Nullable CreativeModeTab defaultTab) {
        if (defaultTab != null)
            tab = defaultTab;
        INSTANCES.put(modId, this);
        Log.debug("First Registration Stage");
        StaticInitializer.initialize(modId, Stage.FIRST);
        Registries.annotations(modId);
        Registries.register(modId);
        DistExecutor.unsafeCallWhenOn(Dist.CLIENT, ()->()->{ ClientRegistries.register(modId); return true; });
        MinecraftForge.EVENT_BUS.register(this);
        Configs.register(modId, name.replace(" ", "_"));
        NetworkManager.register(modId);
        SaveDataManager.annotations(modId);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(CuriosIntegration::enqueueIMC);
        Log.debug("Last Registration Stage");
        StaticInitializer.initialize(modId, Stage.LAST);
//        ConstructorEnforcer.testAllClass(modId);
    }

    public CreativeModeTab tab() { return tab; }
    public Item.Properties defaultProps() { return new Item.Properties().tab(tab); }

    public static <I extends Item, S extends Supplier<I>> CreativeModeTab createTab(String modId, Supplier<S> delayedRegistryItem) {
        return new CreativeModeTab("tab_" + modId) {
            @Override
            @Nonnull
            public ItemStack makeIcon() { return new ItemStack(delayedRegistryItem.get().get()); }
        };
    }
}
