package com.limachi.lim_lib.registries;

import com.limachi.lim_lib.ModAnnotation;
import com.limachi.lim_lib.Strings;
import com.limachi.lim_lib.reflection.Classes;
import com.limachi.lim_lib.registries.annotations.EntityAttributeBuilder;
import com.limachi.lim_lib.registries.clientAnnotations.RegisterItemModelProperty;
import com.limachi.lim_lib.registries.clientAnnotations.RegisterMenuScreen;
import com.limachi.lim_lib.registries.clientAnnotations.RegisterSkin;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
//import net.minecraftforge.client.event.ColorHandlerEvent; //VERSION 1.18.2
import net.minecraftforge.client.event.RegisterColorHandlersEvent; //VERSION 1.19.2
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue", "deprecation"})
public class ClientRegistries {
    protected static final HashMap<RegistryObject<?>, RenderType> RENDER_LAYERS = new HashMap<>();
    protected static final HashMap<RegistryObject<Block>, BlockColor> BLOCK_COLORS = new HashMap<>();
    protected static final HashMap<RegistryObject<Item>, ItemColor> ITEM_COLORS = new HashMap<>();
    protected static final HashMap<ModelLayerLocation, Supplier<LayerDefinition>> LAYER_DEFINITIONS = new HashMap<>();
    protected static final ArrayList<OpaqueMenuScreenRegistry<?, ?>> MENU_SCREEN = new ArrayList<>();
    protected static final ArrayList<OpaqueBERRegistry<?>> BER = new ArrayList<>();
    protected static final ArrayList<OpaqueEntityRendererRegistry<?>> ER = new ArrayList<>();
    protected static final HashMap<RegistryObject<Item>, HashMap<ResourceLocation, ItemPropertyFunction>> ITEM_PROPERTIES = new HashMap<>();


    public static void registerItemModelProperty(RegistryObject<Item> item, ResourceLocation location, ItemPropertyFunction prop) {
        if (!ITEM_PROPERTIES.containsKey(item))
            ITEM_PROPERTIES.put(item, new HashMap<>());
        ITEM_PROPERTIES.get(item).put(location, prop);
    }

    private static void discoverRegisterItemModelProperty(String modId) {
        for (ModAnnotation a : ModAnnotation.iterModAnnotations(modId, RegisterItemModelProperty.class)) {
            String name = Registries.name(a);
            RegistryObject<Item> item = Registries.getRegistryObject(modId, Item.class, name);
            registerItemModelProperty(item, new ResourceLocation(modId, name), (ItemPropertyFunction)((s, l, e, i)-> {
                try {
                    return (float)a.getAnnotatedMethod(ItemStack.class, ClientLevel.class, LivingEntity.class, int.class).invoke(null, s, l, e, i);
                } catch (IllegalAccessException | InvocationTargetException ignored) {
                }
                return 0;
            }));
        }
    }

    protected record OpaqueBERRegistry<T extends BlockEntity>(RegistryObject<BlockEntityType<T>> gbe, BlockEntityRendererProvider<T> gr) {
        void register(EntityRenderersEvent.RegisterRenderers event) { event.registerBlockEntityRenderer(gbe.get(), gr); }
    }

    protected record OpaqueEntityRendererRegistry<T extends Entity>(RegistryObject<EntityType<T>> ge, EntityRendererProvider<T> g) {
        void register(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ge.get(), g); }
    }

    public static <T extends BlockEntity> void setBer(RegistryObject<BlockEntityType<T>> getBe, BlockEntityRendererProvider<T> getRenderer) { BER.add(new OpaqueBERRegistry<>(getBe, getRenderer)); }
    public static <T extends Entity> void setEntityRenderer(RegistryObject<EntityType<T>> getBe, EntityRendererProvider<T> getRenderer) { ER.add(new OpaqueEntityRendererRegistry<>(getBe, getRenderer)); }

    protected static final HashMap<RegistryObject<MenuType<?>>, MenuScreens.ScreenConstructor<?, ?>> CONTAINER_SCREENS = new HashMap<>();

//    public static void setRenderLayer(RegistryObject<Block> rb, RenderType type) { RENDER_LAYERS.put(rb, type); } FIXME: see ItemBlockRenderTypes.setRenderLayer in 1.19.2
    public static void setColor(RegistryObject<Block> rb, BlockColor color) { BLOCK_COLORS.put(rb, color); }
//    public static void setTranslucent(RegistryObject<Block> rb) { setRenderLayer(rb, RenderType.translucent()); } FIXME: see ItemBlockRenderTypes.setRenderLayer in 1.19.2
//    public static void setCutout(RegistryObject<Block> rb) { setRenderLayer(rb, RenderType.cutout()); } FIXME: see ItemBlockRenderTypes.setRenderLayer in 1.19.2
    public static void setLayerDefinition(ModelLayerLocation location, Supplier<LayerDefinition> layerDef) { LAYER_DEFINITIONS.put(location, layerDef); }

    @SubscribeEvent
    static void clientSetup(final FMLClientSetupEvent event)
    {
        for (Map.Entry<RegistryObject<?>, RenderType> entry : RENDER_LAYERS.entrySet()) {
            Object o = entry.getKey().get();
//            if (o instanceof Block)
//                ItemBlockRenderTypes.setRenderLayer((Block)o, entry.getValue());
            if (o instanceof Fluid)
                ItemBlockRenderTypes.setRenderLayer((Fluid)o, entry.getValue());
        }
        for (OpaqueMenuScreenRegistry<?, ?> entry : MENU_SCREEN) entry.register();
        for (Map.Entry<RegistryObject<Item>, HashMap<ResourceLocation, ItemPropertyFunction>> e : ITEM_PROPERTIES.entrySet()) {
            Item item = e.getKey().get();
            for (Map.Entry<ResourceLocation, ItemPropertyFunction> rf : e.getValue().entrySet())
                ItemProperties.register(item, rf.getKey(), rf.getValue());
        }
    }

    @SubscribeEvent
//    static void registerBlockColor(ColorHandlerEvent.Block event) { //VERSION 1.18.2
    static void registerBlockColor(RegisterColorHandlersEvent.Block event) { //VERSION 1.19.2
        BlockColors blockcolors = event.getBlockColors();
        for (Map.Entry<RegistryObject<Block>, BlockColor> entry : BLOCK_COLORS.entrySet()) blockcolors.register(entry.getValue(), entry.getKey().get());
    }

    @SubscribeEvent
//    static void registerBlockColor(ColorHandlerEvent.Item event) { //VERSION 1.18.2
    static void registerBlockColor(RegisterColorHandlersEvent.Item event) { //VERSION 1.19.2
        ItemColors blockcolors = event.getItemColors();
        for (Map.Entry<RegistryObject<Item>, ItemColor> entry : ITEM_COLORS.entrySet()) blockcolors.register(entry.getValue(), entry.getKey().get());
    }

    private static final ArrayList<Constructor<?>> SKINS = new ArrayList<>();
    private static void discoverRegisterSkin(String modId) {
        for (ModAnnotation a : ModAnnotation.iterModAnnotations(modId, RegisterSkin.class)) {
            String name = Registries.name(a);
            if (Registries.skip(a, RegisterSkin.class, name)) continue;
            SKINS.add(a.getAnnotatedClassConstructor(PlayerRenderer.class, EntityModelSet.class));
        }
    }

    protected record OpaqueMenuScreenRegistry<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>>(RegistryObject<MenuType<M>> menu, Class<S> clazz) {
        void register() { MenuScreens.register(menu.get(), new MenuScreens.ScreenConstructor<M, S>() {
                @Override
                public S create(@Nonnull M menu, @Nonnull Inventory inventory, @Nonnull Component title) {
                    return Classes.newClass(clazz, menu, inventory, title);
                }
            });
        }
    }
    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void menuScreen(RegistryObject<MenuType<M>> menu, Class<S> clazz) { MENU_SCREEN.add(new OpaqueMenuScreenRegistry<>(menu, clazz)); }

    private static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void discoverRegisterMenuScreen(String modId) {
        for (ModAnnotation a : ModAnnotation.iterModAnnotations(modId, RegisterMenuScreen.class)) {
            String name = Registries.name(a);
            if (Registries.skip(a, RegisterMenuScreen.class, name)) continue;
            menuScreen(Registries.getMenuType(modId, a.getData("menu", name.replace("_screen", "_menu"))), (Class<S>) a.getAnnotatedClass());
        }
    }

    @SubscribeEvent
    static void registerLayersRenderers(EntityRenderersEvent.AddLayers event) {
        EntityModelSet models = event.getEntityModels();
        for (String rp : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(rp);
            if (renderer != null) {
//                for (Constructor<?> c : SKINS) {
//                    try {
//                        renderer.addLayer((RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)c.newInstance(renderer, models));
//                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    }

    @SubscribeEvent
    static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> e : LAYER_DEFINITIONS.entrySet()) event.registerLayerDefinition(e.getKey(), e.getValue());
    }

    @SubscribeEvent
    static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        for (OpaqueEntityRendererRegistry<?> er : ER) er.register(event);
        for (OpaqueBERRegistry<?> ber : BER) ber.register(event);
    }

    public static void register(String modId) {
        discoverRegisterSkin(modId);
        discoverRegisterMenuScreen(modId);
        discoverRegisterItemModelProperty(modId);
    }
}
