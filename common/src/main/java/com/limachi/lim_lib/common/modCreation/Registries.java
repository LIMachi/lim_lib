package com.limachi.lim_lib.common.modCreation;

import com.limachi.lim_lib.InstancedMod;
import com.limachi.lim_lib.common.annotations.*;
import com.limachi.lim_lib.common.dataStorage.DataField;
import com.limachi.lim_lib.common.dataStorage.LevelDataFile;
import com.limachi.lim_lib.common.utils.StringUtils;
import com.limachi.lim_lib.common.codec.CodecUtils;
import com.limachi.lim_lib.common.network.ClassMsg;
import com.limachi.lim_lib.common.network.IC2SMsg;
import com.limachi.lim_lib.common.network.IMsg;
import com.limachi.lim_lib.common.network.IS2CMsg;
import com.limachi.lim_lib.common.reflect.FieldAccess;
import com.limachi.lim_lib.common.reflect.MethodAccess;
import com.limachi.lim_lib.common.reflect.ReflectUtils;

import com.limachi.lim_lib.common.utils.Game;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@SuppressWarnings({"unchecked", "unused"})
public class Registries {
    public final InstancedMod mod;
    public final String mod_id;

    public final DeferredRegister<DataComponentType<?>> component_types;
    public final DeferredRegister<Block> blocks;
    public final DeferredRegister<BlockEntityType<?>> block_entities;
    public final DeferredRegister<Item> items;
    public final DeferredRegister<MenuType<?>> menus;
    public final DeferredRegister<CreativeModeTab> tabs;
    public final DeferredRegister<EntityType<?>> entities;

    public RegistrySupplier<CreativeModeTab> default_tab = null;
    public final HashMap<Class<?>, Pair<CustomPacketPayload.Type<?>, CustomPacketPayload.Type<?>>> messages = new HashMap<>();

    public <T extends IMsg<T>> CustomPacketPayload.Type<T> getMessageType(IMsg<T> msg) {
        return (CustomPacketPayload.Type<T>) Optional.ofNullable(messages.get(msg.getClass())).map(p->msg.upstream() ? p.getFirst() : p.getSecond()).orElse(null);
    }

    protected <T extends IMsg<T>> boolean messageHasReceiver(Class<T> msg) {
        return IS2CMsg.class.isAssignableFrom(msg) || IC2SMsg.class.isAssignableFrom(msg);
    }

    protected <T extends IMsg<T>> void registerMessageReceivers(Class<T> msg, ResourceLocation id, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        boolean s2c = IS2CMsg.class.isAssignableFrom(msg);
        boolean c2s = IC2SMsg.class.isAssignableFrom(msg);
        CustomPacketPayload.Type<T> s2cType = s2c ? new CustomPacketPayload.Type<>(c2s ? ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_s2c") : id) : null;
        CustomPacketPayload.Type<T> c2sType = c2s ? new CustomPacketPayload.Type<>(s2c ? ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath() + "_c2s") : id) : null;
        if (s2c)
            Game.runPhysical(()->()->NetworkManager.registerReceiver(NetworkManager.Side.S2C, s2cType, codec, T::run), ()->()->NetworkManager.registerS2CPayloadType(s2cType, codec));
        if (c2s)
            NetworkManager.registerReceiver(NetworkManager.Side.C2S, c2sType, codec, T::run);
        messages.put(msg, new Pair<>(c2sType, s2cType));
    }

    public <T extends IMsg<T>> void recordMessage(Class<T> clazz, ResourceLocation id) {
        if (!messageHasReceiver(clazz)) {
            mod.logger.error("message registration without receiver declared (should extend one or more of IS2CMsg/IC2SMsg): " + id);
            return;
        }
        if (!Record.class.isAssignableFrom(clazz)) {
            mod.logger.error("recordMessage registration called for non record object: " + clazz);
            return;
        }
        registerMessageReceivers(clazz, id, CodecUtils.autoStreamCodec(clazz));
        logRegistration("message", id);
    }

    public <T extends ClassMsg<T>> void dynamicMessage(Class<T> clazz, ResourceLocation id) {
        if (!messageHasReceiver(clazz)) {
            mod.logger.error("message registration without receiver declared (should extend one or more of IS2CMsg/IC2SMsg): " + id);
            return;
        }
        registerMessageReceivers(clazz, id, CustomPacketPayload.codec(ClassMsg::write, i -> ReflectUtils.unsafeInstance(clazz).read(i)));
        logRegistration("message", id);
    }

    protected void error(String error) { mod.logger.error(error); }

    protected String logRegistration(String kind, String id) {
        mod.logger.info("registered " + kind + ": " + id);
        return id;
    }

    protected ResourceLocation logRegistration(String kind, ResourceLocation id) {
        if (id.getNamespace().equals(mod_id))
            logRegistration(kind, id.getPath());
        else
            logRegistration(kind, id.toString());
        return id;
    }

    protected <T> RegistrySupplier<T> logRegistration(String kind, RegistrySupplier<T> sup) {
        logRegistration(kind, ResourceLocation.parse(sup.getRegisteredName()));
        return sup;
    }

    public Registries(String mod_id, InstancedMod mod) {
        this.mod = mod;
        this.mod_id = mod_id;
        mod.logger.info("Started registration for mod: " + mod_id);
        component_types = DeferredRegister.create(mod_id, net.minecraft.core.registries.Registries.DATA_COMPONENT_TYPE);
        blocks = DeferredRegister.create(mod_id, net.minecraft.core.registries.Registries.BLOCK);
        block_entities = DeferredRegister.create(mod_id, net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE);
        items = DeferredRegister.create(mod_id, net.minecraft.core.registries.Registries.ITEM);
        menus = DeferredRegister.create(mod_id, net.minecraft.core.registries.Registries.MENU);
        tabs = DeferredRegister.create(mod_id, net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB);
        entities = DeferredRegister.create(mod_id, net.minecraft.core.registries.Registries.ENTITY_TYPE);
    }

//    public <T extends ModBase> T initMod() {
//        try {
//            return (T)mod.newInstance();
//        } catch (Exception e) {
//            return null; //FIXME
//        }
//    }

    public void register() {
        component_types.register();
        blocks.register();
        block_entities.register();
        items.register();
        menus.register();
        tabs.register();
        entities.register();
        mod.logger.info("finished common registration");
    }

    public static Comparator<String> longestString = Comparator.comparing(String::length).reversed().thenComparing(Comparator.naturalOrder());
    public static HashMap<Class<?>, TreeSet<String>> DISCARD_SUFFIXES = new HashMap<>();

    public static void addDiscardSuffixes(Class<?> clazz, String ... suffixes) {
        DISCARD_SUFFIXES.compute(clazz, (k, v)->{
            if (v == null)
                v = new TreeSet<>(longestString);
            v.addAll(List.of(suffixes));
            return v;
        });
    }

    static {
//        addDiscardSuffixes(Item.class, "_item", "_block_item", "_block", "_block_entity", "_be", "_b_e", "_i", "_b");
//        addDiscardSuffixes(Block.class, "_item", "_block_item", "_block", "_block_entity", "_be", "_b_e", "_i", "_b");
        addDiscardSuffixes(BlockEntity.class, "_block_entity", "_be", "_b_e");
        addDiscardSuffixes(IMsg.class, "_msg", "_message");
        addDiscardSuffixes(AbstractContainerMenu.class, "_menu", "_screen", "_menu_screen");
    }

    public static String discardSuffixes(Class<?> clazz, String input) {
        for (var e : DISCARD_SUFFIXES.entrySet())
            if (e.getKey().isAssignableFrom(clazz)) {
                boolean discard = true;
                while (discard) {
                    discard = false;
                    for (String suffix : e.getValue())
                        if (input.endsWith(suffix)) {
                            discard = true;
                            input = input.substring(0, input.length() - suffix.length());
                        }
                }
            }
        return input;
    }

    public static String defaultToClass(String nullable, Class<?> clazz) {
        if (nullable == null || nullable.isBlank())
            nullable = discardSuffixes(clazz, StringUtils.camelToSnake(StringUtils.getSimplifiedClassName(clazz.getName())));
        return nullable;
    }

    public static String defaultToMethod(String nullable, MethodAccess<?, ?> m) {
        if (nullable == null || nullable.isBlank())
            return StringUtils.camelToSnake(m.name());
        return nullable;
    }

    public static String defaultToField(String nullable, FieldAccess<?, ?> f) {
        if (nullable == null || nullable.isBlank())
            return StringUtils.camelToSnake(f.name());
        return nullable;
    }

    public static <T, S> Supplier<S> defaultInstanceSupplier(Class<T> clazz, Class<S> sup) {
        Constructor<T> c;
        if (!sup.isAssignableFrom(clazz)) {
            System.err.println("invalid cast from " + clazz + " to " + sup);
            System.exit(-1);
            return null;
        }
        try {
            c = clazz.getConstructor();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
        return ()->{
            try {
                return (S)c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
                return null;
            }
        };
    }

    public static boolean hasConstructor(Class<?> clazz, Class<?> ... parameterTypes) {
        try {
            clazz.getConstructor(parameterTypes);
            return true;
        } catch (NoSuchMethodException | SecurityException ignore) {
            return false;
        }
    }

    public static <T, S, P> Function<P, S> defaultInstanceSupplier(Class<T> clazz, Class<S> sup, Class<P> param) {
        Constructor<T> c;
        if (!sup.isAssignableFrom(clazz)) {
            System.err.println("invalid cast from " + clazz + " to " + sup);
            System.exit(-1);
            return null;
        }
        try {
            c = clazz.getConstructor(param);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
        return p->{
            try {
                return (S)c.newInstance(p);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
                return null;
            }
        };
    }

    public static <T, S, P0, P1> BiFunction<P0, P1, S> defaultInstanceSupplier(Class<T> clazz, Class<S> sup, Class<P0> param0, Class<P1> param1) {
        Constructor<T> c;
        if (!sup.isAssignableFrom(clazz)) {
            System.err.println("invalid cast from " + clazz + " to " + sup);
            System.exit(-1);
            return null;
        }
        try {
            c = clazz.getConstructor(param0, param1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
        return (p0, p1)->{
            try {
                return (S)c.newInstance(p0, p1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
                return null;
            }
        };
    }

    public static final String[] DEFAULT_TABS = new String[]{"automatic"};

    public <T extends Item> RegistrySupplier<T> item(String reg_key, Function<Item.Properties, T> n, String infoKey) {
        return item(reg_key, n, infoKey, DEFAULT_TABS);
    }

    public <T extends Item> RegistrySupplier<T> item(String reg_key, Function<Item.Properties, T> n) {
        return item(reg_key, n, null, DEFAULT_TABS);
    }

    public <T extends Item> RegistrySupplier<T> item(String reg_key, Function<Item.Properties, T> n, String[] tab) {
        return item(reg_key, n, null, tab);
    }

    public <T extends Item> RegistrySupplier<T> item(String reg_key, Function<Item.Properties, T> n, String infoKey, String[] tab) {
        var out = items.register(reg_key, ()->n.apply(new Item.Properties()));
//        if (out != null && infoKey != null && !infoKey.isBlank())
//            JEIInfo.registerInfo(out, infoKey);
        if (tab != null && out != null)
            for (String t : tab)
                if (t != null && !t.isBlank()) {
                    RegistrySupplier<CreativeModeTab> ts;
                    if (t.equals("automatic"))
                        ts = default_tab;
                    else
                        ts = tabs.getRegistrar().delegate(ResourceLocation.parse(t));
                    if (ts != null)
                        CreativeTabRegistry.append(ts, out);
                }
        return logRegistration("item", out);
    }

    public <T> RegistrySupplier<DataComponentType<T>> component(String reg_key, Codec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return logRegistration("component type", component_types.register(reg_key, ()->{
            DataComponentType.Builder<T> builder = DataComponentType.builder();
            return builder.persistent(codec).networkSynchronized(streamCodec).build();
        }));
    }

    public <T extends Block> RegistrySupplier<T> block(String reg_key, Supplier<T> n) {
        return logRegistration("block", blocks.register(reg_key, n));
    }

    public <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> blockEntity(String reg_key, BlockEntityType.BlockEntitySupplier<T> n, Supplier<Block> ... blocks) {
        return logRegistration("block entity", block_entities.register(reg_key, ()->{
            Block[] b = new Block[blocks.length];
            for (int i = 0; i < blocks.length; ++i)
                b[i] = blocks[i].get();
            return BlockEntityType.Builder.of(n, b).build(null);
        }));
    }

    public <T extends Entity> RegistrySupplier<EntityType<T>> entity(String reg_key, EntityType.Builder<T> builder) {
        return logRegistration("entity", entities.register(reg_key, ()->builder.build(reg_key)));
    }

    protected void extractItems() {
        mod.extractor.runOnFields(RegisterItem.class, (f, a)->{
            String name = defaultToClass(a.value(), f.clazz());
            if (hasConstructor(f.clazz(), Item.Properties.class))
                ((FieldAccess<?, RegistrySupplier<Item>>) f).set(null, false, item(name, defaultInstanceSupplier(f.clazz(), Item.class, Item.Properties.class), a.jeiInfoKey(), a.tab()));
            else
                mod.logger.error("@RegisterItem on a class that does not have a Object(Item.Properties) constructor exposed: " + f.clazz());
        });
    }

    protected void extractBlocks() {
        mod.extractor.runOnFields(RegisterBlock.class, (f, a)->{
            String name = defaultToClass(a.value(), f.clazz());
            ((FieldAccess<?, RegistrySupplier<Block>>) f).set(null, false, block(name, defaultInstanceSupplier(f.clazz(), Block.class)));
        });
    }

    protected void extractBlockItems() {
        mod.extractor.runOnFields(RegisterBlockItem.class, (f, a)-> {
            String name = defaultToClass(a.value(), f.clazz());
            String block = defaultToClass(a.block(), f.clazz());
            ((FieldAccess<?, RegistrySupplier<BlockItem>>) f).set(null, false, item(name,
                    p->new BlockItem(blocks.getRegistrar().get(ResourceLocation.fromNamespaceAndPath(mod_id, block)), p),
                    a.jeiInfoKey(), a.tab()));
            f.type();
        });
    }

    public static <R, T extends R> RegistrySupplier<T> searchRegistry(DeferredRegister<R> register, String regex) {
        var pattern = Pattern.compile(regex);
        for (var r : register)
            if (pattern.matcher(r.getId().toString()).matches())
                return (RegistrySupplier<T>) r;
        return null;
    }

    protected void extractBlockEntities() {
        mod.extractor.runOnFields(RegisterBlockEntity.class, (f, a)->{
            String name = defaultToClass(a.value(), f.clazz());
            Supplier<Block>[] sba;
            if (a.blocks().length == 0) {
                sba = new Supplier[1];
                sba[0] = searchRegistry(blocks, mod_id + ":" + name);
                if (sba[0] == null) {
                    mod.logger.error("could not find block for block entity: " + name);
                    return;
                }
            } else {
                sba = new Supplier[a.blocks().length];
                for (int i = 0; i < a.blocks().length; ++i) {
                    var n = ResourceLocation.parse(a.blocks()[i]);
                    if (n.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE))
                        n = ResourceLocation.fromNamespaceAndPath(mod_id, a.blocks()[i]);
                    for (var r : blocks)
                        if (r.is(n)) {
                            sba[i] = r;
                            break;
                        }
                    if (sba[i] == null) {
                        mod.logger.error("could not find block \"" + n +  "\" for block entity: " + name);
                        return;
                    }
                }
            }
            BiFunction<BlockPos, BlockState, BlockEntity> sup = defaultInstanceSupplier(f.clazz(), BlockEntity.class, BlockPos.class, BlockState.class);
            ((FieldAccess<?, RegistrySupplier<BlockEntityType<BlockEntity>>>) f).set(null, false, blockEntity(name, sup::apply, sba));
        });
    }

    protected void extractTabs() {
        mod.extractor.runOnMethods(RegisterTab.class, (m, a)->{
            var t = logRegistration("tab", tabs.register(defaultToMethod(a.value(), m), ()->CreativeTabRegistry.create(c->m.get(null, false, c))));
            if (a.defaultTab())
                default_tab = t;
        });
    }

    protected void extractMsgs() {
        mod.extractor.runOnClasses(RegisterMsg.class, (c, a)->{
            if (!IMsg.class.isAssignableFrom(c)) {
                error("@RegisterMsg not on a class/record that extend/implement ClassMsg/IMsg" + c);
                return;
            }
            String name = defaultToClass(a.value(), c);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(mod_id, name);
            if (Record.class.isAssignableFrom(c))
                recordMessage((Class<IMsg>)c, id);
            else if (ClassMsg.class.isAssignableFrom(c))
                dynamicMessage((Class<ClassMsg>)c, id);
            else
                error("@RegisterMsg not on a class/record that extend/implement ClassMsg/IMsg" + c);
        });
    }

    protected void extractEvents() {
        mod.extractor.runOnMethods(RegisterEventListener.class, (m, a)->a.value().register(m));
    }

    public <T extends AbstractContainerMenu> RegistrySupplier<MenuType<T>> menu(String reg_key, MenuRegistry.ExtendedMenuTypeFactory<T> builder) {
        return logRegistration("menu", menus.register(reg_key, ()->MenuRegistry.ofExtended(builder)));
    }

    protected void extractMenus() {
        mod.extractor.runOnFields(RegisterMenu.class, (f, a)->{
            final Constructor<AbstractContainerMenu> ctr = (Constructor<AbstractContainerMenu>) ReflectUtils.getMatchingConstructor(f.clazz(), int.class, Inventory.class, RegistryFriendlyByteBuf.class);
            ((FieldAccess<?, RegistrySupplier<MenuType<AbstractContainerMenu>>>)f).set(null, false, menu(defaultToClass(a.value(), f.clazz()), (id, inventory, buf) -> ReflectUtils.nullableInstance(ctr, id, inventory, buf)));
        });
    }

    protected void extractLevelDataFields() {
        HashMap<String, LevelDataFile> tmp = new  HashMap<>();
        mod.extractor.runOnFields(RegisterData.class, (f, a) -> {
            if (f.get() instanceof DataField<?> ldf) {
                String path = f.clazz().toString() + "#" + f.name();
                ldf.annotation(mod, a, path);
            }
        });
    }

    public static boolean contains(DeferredRegister<?> reg, ResourceLocation loc) {
        for (RegistrySupplier<?> registrySupplier : reg)
            if (registrySupplier.is(loc))
                return true;
        return false;
    }

    protected void extractEntityAttributes() {
        mod.extractor.runOnMethods(EntityAttributeBuilder.class, (m, a)->{
            var name = ResourceLocation.fromNamespaceAndPath(mod_id, defaultToClass(a.value(), m.clazz()));
            if (contains(entities, name)) {
                var entity = entities.getRegistrar().delegate(name);
                EntityAttributeRegistry.register((RegistrySupplier<EntityType<LivingEntity>>)(Object)entity, ()-> (AttributeSupplier.Builder) m.get());
            } else {
                //error
            }
        });
    }

    protected void extractEntities() {
        mod.extractor.runOnFields(RegisterEntity.class, (f, a)->{
            if (hasConstructor(f.clazz(), EntityType.class, Level.class)) {
                String name = defaultToClass(a.value(), f.clazz());
                var builder = defaultInstanceSupplier(f.clazz(), Entity.class, EntityType.class, Level.class);
                ((FieldAccess<?, RegistrySupplier<EntityType<Entity>>>) f).set(null, false, entity(name, EntityType.Builder.of(builder::apply, a.category()).sized(a.width(), a.height())));
            } else
                mod.logger.error("@RegisterEntity on a class that does not have a Object(EntityType, Level) constructor exposed: " + f.clazz());
        });
    }

    protected void extractRecipes() {}

    protected void stage(Stage stage, Runnable run) {
        StaticInitializer.initialize(mod.extractor, stage, true);
        run.run();
        StaticInitializer.initialize(mod.extractor, stage, false);
    }

    public void extractInStages() {
        synchronized (this) {
            stage(Stage.EVENTS, this::extractEvents);
            stage(Stage.MSG, this::extractMsgs);
            stage(Stage.TAB, this::extractTabs);
            stage(Stage.BLOCK, this::extractBlocks);
            stage(Stage.BLOCK_ITEM, this::extractBlockItems);
            stage(Stage.ITEM, this::extractItems);
            stage(Stage.BLOCK_ENTITY, this::extractBlockEntities);
            stage(Stage.ENTITY, this::extractEntities);
            stage(Stage.ENTITY_ATTRIBUTE, this::extractEntityAttributes);
            stage(Stage.RECIPES, this::extractRecipes);
            stage(Stage.MENU, this::extractMenus);
            stage(Stage.LEVEL_DATA_FIELDS, this::extractLevelDataFields);
        }
    }
}
