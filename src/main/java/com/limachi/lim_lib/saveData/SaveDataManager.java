package com.limachi.lim_lib.saveData;

import com.limachi.lim_lib.*;
import com.limachi.lim_lib.network.NetworkManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class SaveDataManager {
    protected static HashMap<String, Class<? extends AbstractSyncSaveData>> SAVE_DATAS = new HashMap<>();
    protected static final HashMap<Pair<String, String>, AbstractSyncSaveData> CLIENT_INSTANCES = new HashMap<>();

    public static void register(String name, Class<? extends AbstractSyncSaveData> dataClass) {
        SAVE_DATAS.put(name, dataClass);
    }

    @SuppressWarnings("unchecked")
    public static void annotations(String modId) {
        for (ModAnnotation a : ModAnnotation.iterModAnnotations(modId, RegisterSaveData.class)) {
            String name = a.getData("name", "");
            if (name.equals(""))
                name = Strings.camelToSnake(Strings.getFile('.', a.getAnnotatedClass().getCanonicalName())).replace("_save_data", "").replace("_data", "");
            register(name, (Class<? extends AbstractSyncSaveData>)a.getAnnotatedClass());
        }
    }

    public static void serverUpdate(Player player, String name, String level, CompoundTag nbt, boolean isDiff) {
        execute(name, World.getLevel(level).dimension(), s->{
            if (isDiff)
                s.applyDiff(nbt);
            else
                s.load(nbt);
            if (!Sides.isLogicalClient())
                s.setDirty();
        });
    }

    public static void clientUpdate(String name, String level, CompoundTag nbt, boolean isDiff) {
        AbstractSyncSaveData d = CLIENT_INSTANCES.computeIfAbsent(new Pair<>(name, level), s -> {
            String type = Strings.getFolder(':', name);
            if (type.equals("")) type = name;
            if (!SAVE_DATAS.containsKey(type)) return null;
            try {
                AbstractSyncSaveData td = SAVE_DATAS.get(type).getConstructor(String.class).newInstance(name);
                td.level = level;
                return td;
            } catch (Exception e) {
                return null;
            }
        });
        if (d != null) {
            if (isDiff)
                d.applyDiff(nbt);
            else
                d.load(nbt);
        }
    }

    public static <T extends AbstractSyncSaveData> T getInstance(String name) { return getInstance(name, null, false); }
    public static <T extends AbstractSyncSaveData> T getInstance(String name, ResourceKey<Level> level) { return getInstance(name, level, false); }
    @SuppressWarnings("unchecked")
    public static <T extends AbstractSyncSaveData> T getInstance(String name, ResourceKey<Level> level, boolean getOnly) {
        if (Sides.isLogicalClient()) return (T)CLIENT_INSTANCES.get(new Pair<>(name, level.location().toString()));
        String type = Strings.getFolder(':', name);
        if (type.equals("")) type = name;
        Class<T> clazz = (Class<T>)SAVE_DATAS.get(type);
        if (clazz != null) {
            if (level == null)
                level = Level.OVERWORLD;
            Level finalLevel = World.getLevel(level);
            if (finalLevel != null) {
                Supplier<T> supp = () -> {
                    try {
                        return (T) clazz.getConstructor(String.class).newInstance(name).setLevel(finalLevel);
                    } catch (Exception e) {
                        return null;
                    }
                };
                Function<CompoundTag, T> read = nbt -> {
                    T t = supp.get();
                    t.load(nbt);
                    return t;
                };
                DimensionDataStorage storage = ((ServerLevel)finalLevel).getDataStorage();
                if (!getOnly)
                    return storage.computeIfAbsent(read, supp, name.replace(':', '_'));
                else
                    return storage.get(read, name.replace(':', '_'));
            }
        }
        return null;
    }


    public static <T, S extends AbstractSyncSaveData> T execute(String name, Function<S, T> exec, Supplier<T> onError) { return execute(name, null, exec, onError); }
    public static <T, S extends AbstractSyncSaveData> T execute(String name, ResourceKey<Level> level, Function<S, T> exec, Supplier<T> onError) {
        S instance = getInstance(name, level);
        if (instance != null)
            return exec.apply(instance);
        return onError.get();
    }

    public static <S extends AbstractSyncSaveData> void execute(String name, Consumer<S> exec) { execute(name, null, exec); }
    public static <S extends AbstractSyncSaveData> void execute(String name, ResourceKey<Level> level, Consumer<S> exec) {
        S instance = getInstance(name, level);
        if (instance != null)
            exec.accept(instance);
    }

    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event
//                .getPlayer() //VERSION 1.18.2
                .getEntity() // VERSION 1.19.2
                .level.isClientSide()) {
            for (String k : SAVE_DATAS.keySet()) {
                for (Level level : World.getAllLevels()) {
                    DimensionDataStorage storage = ((ServerLevel) level).getDataStorage();
                    for (String fileName : storage.dataFolder.list()) {
                        fileName = fileName.replace(".dat", "");
                        if (fileName.startsWith(k + "_") || fileName.equals(k)) {
                            fileName = fileName.replaceFirst(k + "_", k + ":");
                            AbstractSyncSaveData d = getInstance(fileName, level.dimension(), true);
                            if (d != null)
                                NetworkManager.toClient(ModBase.COMMON_ID, (ServerPlayer) event
//                            .getPlayer() // VERSION 1.18.2
                                                .getEntity() // VERSION 1.19.2
                                        , d.pack(true));
                        }
                    }
                }
            }
        } else
            CLIENT_INSTANCES.clear();
    }

    @SubscribeEvent
    public static void onPlayerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event
//                .getPlayer() // VERSION 1.18.2
                .getEntity() // VERSION 1.19.2
                .level.isClientSide())
            CLIENT_INSTANCES.clear();
    }
}
