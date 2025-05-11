package com.limachi.lim_lib.client.modCreation;

import com.limachi.lim_lib.InstancedMod;
import com.limachi.lim_lib.client.annotations.*;
//import com.limachi.lim_lib.common.mod_creation.ModBase;
import com.limachi.lim_lib.common.modCreation.Registries;
import com.limachi.lim_lib.common.modCreation.StaticInitializer;
import com.limachi.lim_lib.common.reflect.ReflectUtils;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.RegistrySupplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "unused"})
@Environment(EnvType.CLIENT)
public class ClientRegistries {
    public final InstancedMod mod;
    protected HashMap<ResourceLocation, BlockColor> blockTints = new HashMap<>();
    protected HashMap<ResourceLocation, ItemColor> itemTints = new HashMap<>();

    public ClientRegistries(InstancedMod mod) {
        this.mod = mod;
    }

    public void registerBlockTint(BlockColor blockTint, ResourceLocation ... ids) {
        for (var id : ids)
            blockTints.put(id, blockTint);
    }

    public void registerItemTint(ItemColor itemTint, ResourceLocation ... ids) {
        for (var id : ids)
            itemTints.put(id, itemTint);
    }

    static {
        Registries.addDiscardSuffixes(Screen.class, "_menu", "_screen", "_menu_screen");
    }

    protected void extractBlockTinters() {
        mod.extractor.runOnMethods(BlockTinter.class, (m, a)->{
            var id = ResourceLocation.fromNamespaceAndPath(mod.registries.mod_id, Registries.defaultToClass(a.value(), m.clazz()));
            ColorHandlerRegistry.registerBlockColors((s, g, p, i)->(int)m.get(null, false, s, g, p, i), ()->mod.registries.blocks.getRegistrar().get(id));
        });
        mod.extractor.runAnnotations(HasRedstoneTint.class, (c, a)->{
            if (Block.class.isAssignableFrom(c)) {
                var id = ResourceLocation.fromNamespaceAndPath(mod.registries.mod_id, Registries.defaultToClass(a.value(), c));
                Supplier<Block> t = ()->mod.registries.blocks.getRegistrar().get(id);
                ColorHandlerRegistry.registerBlockColors((s, g, p, i) -> 0xFF000000 | RedStoneWireBlock.getColorForPower(s.getValue(BlockStateProperties.POWER)), t);
            }
        }, (f, a)->{
            if (f.get() instanceof RegistrySupplier<?> rs && rs.getRegistrar().key().location().getPath().equals("block"))
                ColorHandlerRegistry.registerBlockColors((s, g, p, i) -> 0xFF000000 | RedStoneWireBlock.getColorForPower(s.getValue(BlockStateProperties.POWER)), (RegistrySupplier<Block>)rs);
        }, null);
    }

    protected void extractItemTinters() {
        mod.extractor.runOnMethods(ItemTinter.class, (m , a)->{
            var id = ResourceLocation.fromNamespaceAndPath(mod.registries.mod_id, Registries.defaultToClass(a.value(), m.clazz()));
            Supplier<Item> t = ()->mod.registries.items.getRegistrar().get(id);
            ColorHandlerRegistry.registerItemColors((s, i)->(int)m.get(null, false, s, i), t);
        });
        mod.extractor.runAnnotations(HasRedstoneTint.class, (c, a)->{
            if (Item.class.isAssignableFrom(c)) {
                var id = ResourceLocation.fromNamespaceAndPath(mod.registries.mod_id, Registries.defaultToClass(a.value(), c));
                Supplier<Item> t = ()->mod.registries.items.getRegistrar().get(id);
                ColorHandlerRegistry.registerItemColors((s, i) -> 0xFF000000 | RedStoneWireBlock.getColorForPower(15), t);
            }
        }, (f, a)->{
            if (f.get() instanceof RegistrySupplier<?> rs && rs.getRegistrar().key().location().getPath().equals("item"))
                ColorHandlerRegistry.registerItemColors((s, i) -> 0xFF000000 | RedStoneWireBlock.getColorForPower(15), (RegistrySupplier<Item>)rs);
        }, null);
    }

    protected void extractKeyBindings() {
        mod.extractor.runOnFields(RegisterKeyBinding.class, (f, a)->{
            if (f.get() instanceof KeyMapping key)
                KeyMappingRegistry.register(key);
            else
                mod.logger.error("RegisterKeyBinding used on invalid type: " + f.get());
        });
    }

    //dark type casting magic
    //basically, we trick the compiler in thinking our code is sound by putting seemingly valid bounds on interfaces
    //but there is no way to check this at compilation :)
    @Environment(EnvType.CLIENT)
    private record S<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>>(RegistrySupplier<MenuType<M>> menu, Class<S> screen) implements MenuRegistry.ScreenFactory<M, S> {
        static <M0 extends AbstractContainerMenu, S0 extends Screen & MenuAccess<M0>> Class<S0> cast(Class<?> c) { return (Class<S0>) c; }

        @Override
        public S create(M containerMenu, Inventory inventory, Component component) {
            return ReflectUtils.nullableInstance(screen, containerMenu, inventory, component);
        }

        void register() { MenuRegistry.registerScreenFactory(menu.get(), this); }
    }

    protected void extractEvents() {
        mod.extractor.runOnMethods(RegisterClientEventListener.class, (m, a)->a.value().register(m));
    }

    protected void extractMenuScreens() {
        mod.extractor.runOnClasses(RegisterMenuScreen.class, (c, a)->new S<>(Registries.searchRegistry(mod.registries.menus, mod.registries.mod_id + ":" + Registries.defaultToClass(a.value(), c)), S.cast(c)).register());
    }

    protected void stage(ClientStage stage, Runnable run) {
        StaticInitializer.initialize(mod.extractor, stage, true);
        run.run();
        StaticInitializer.initialize(mod.extractor, stage, false);
    }

    public void extractInStages() {
        synchronized (this) {
            stage(ClientStage.EVENTS, this::extractEvents);
            stage(ClientStage.SCREEN, this::extractMenuScreens);
            stage(ClientStage.KEY_BINDING, this::extractKeyBindings);
            stage(ClientStage.BLOCK_TINTER, this::extractBlockTinters);
            stage(ClientStage.ITEM_TINTER, this::extractItemTinters);
        }
    }

    public void register() {
        for (var e : blockTints.entrySet())
            ColorHandlerRegistry.registerBlockColors(e.getValue(), () -> mod.registries.blocks.getRegistrar().get(e.getKey()));
        for (var e : itemTints.entrySet()) {
            Supplier<Item> t = ()->mod.registries.items.getRegistrar().get(e.getKey());
            ColorHandlerRegistry.registerItemColors(e.getValue(), t);
        }
    }
}
