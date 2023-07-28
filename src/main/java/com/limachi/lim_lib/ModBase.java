package com.limachi.lim_lib;

import com.limachi.lim_lib.constructorEnforcer.ConstructorEnforcer;
import com.limachi.lim_lib.integration.Curios.CuriosIntegration;
import com.limachi.lim_lib.network.NetworkManager;
import com.limachi.lim_lib.registries.Stage;
import com.limachi.lim_lib.registries.ClientRegistries;
import com.limachi.lim_lib.registries.Registries;
import com.limachi.lim_lib.registries.StaticInitializer;
import com.limachi.lim_lib.saveData.SaveDataManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ModBase {

    public static final String COMMON_ID = "lim_lib";

    public static final HashMap<String, ModBase> INSTANCES = new HashMap<>();

    protected RegistryObject<CreativeModeTab> tab = RegistryObject.createOptional(CreativeModeTabs.SEARCH.location(), net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, "minecraft");

    public ModBase(@Nonnull String modId, @Nonnull String name, boolean useConstructorEnforcer, @Nullable RegistryObject<CreativeModeTab> defaultTab) {
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
        if (useConstructorEnforcer)
            ConstructorEnforcer.testAllClass(modId);
    }

    public RegistryObject<CreativeModeTab> tab() { return tab; }
    public Item.Properties defaultProps() { return new Item.Properties(); }

    public static <I extends Item, S extends Supplier<I>> RegistryObject<CreativeModeTab> createTab(String modId, String tab, Supplier<S> delayedRegistryItem, ItemLike ... items) {
        return Registries.tab(modId, tab, ()->CreativeModeTab.builder()
                .title(Component.translatable("creative_tab." + modId))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS, CreativeModeTabs.INGREDIENTS)
                .icon(()->new ItemStack(delayedRegistryItem.get().get()))
                .displayItems((p, o)-> o.acceptAll(Arrays.stream(items).map(ItemStack::new).toList()))
                .build());
    }

    public static <I extends Item, S extends Supplier<I>> RegistryObject<CreativeModeTab> createTab(String modId, String tab, Supplier<S> delayedRegistryItem, Collection<ItemLike> items) {
        return Registries.tab(modId, tab, ()->CreativeModeTab.builder()
                .title(Component.translatable("creative_tab." + modId))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS, CreativeModeTabs.INGREDIENTS)
                .icon(()->new ItemStack(delayedRegistryItem.get().get()))
                .displayItems((p, o)->o.acceptAll(items.stream().map(ItemStack::new).toList()))
                .build());
    }
}
