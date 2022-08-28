package com.limachi.lim_lib.saveData;

import com.limachi.lim_lib.*;
import com.limachi.lim_lib.network.NetworkManager;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
    protected static HashMap<String, Pair<Class<? extends AbstractSyncSaveData>, SaveSync>> SAVE_DATAS = new HashMap<>();
    protected static final HashMap<String, AbstractSyncSaveData> CLIENT_INSTANCES = new HashMap<>();

    public static void register(String name, SaveSync sync, Class<? extends AbstractSyncSaveData> dataClass) {
        SAVE_DATAS.put(name, new Pair<>(dataClass, sync));
    }

    @SuppressWarnings("unchecked")
    public static void annotations(String modId) {
        for (ModAnnotation a : ModAnnotation.iterModAnnotations(modId, RegisterSaveData.class)) {
            String name = a.getData("name", "");
            if (name.equals(""))
                name = Strings.camelToSnake(Strings.getFile('.', a.getAnnotatedClass().getCanonicalName())).replace("_save_data", "").replace("_data", "");
            register(name, a.getData("sync", SaveSync.SERVER_TO_CLIENT), (Class<? extends AbstractSyncSaveData>)a.getAnnotatedClass());
        }
    }

    public static void serverUpdate(Player player, String name, CompoundTag nbt, boolean isDiff) {
        execute(name, s->{
            if (isDiff)
                s.applyDiff(nbt);
            else
                s.load(nbt);
            if (!Sides.isLogicalClient())
                s.setDirty();
        });
    }

    public static void clientUpdate(String name, CompoundTag nbt, boolean isDiff) {
        AbstractSyncSaveData d = CLIENT_INSTANCES.computeIfAbsent(name, s -> {
            if (!SAVE_DATAS.containsKey(name)) return null;
            Class<? extends AbstractSyncSaveData> type = SAVE_DATAS.get(name).getFirst();
            try {
                return type.getConstructor(String.class, SaveSync.class).newInstance(name, SAVE_DATAS.get(name).getSecond());
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

    @SuppressWarnings("unchecked")
    public static <T extends AbstractSyncSaveData> T getInstance(String name) {
        if (Sides.isLogicalClient()) return (T)CLIENT_INSTANCES.get(name);
        String type = Strings.getFolder(':', name);
        if (type.equals("")) type = name;
        Class<T> clazz = (Class<T>)SAVE_DATAS.get(type).getFirst();
        SaveSync sync = SAVE_DATAS.get(type).getSecond();
        if (clazz != null) {
            ServerLevel overworld = (ServerLevel) World.overworld();
            if (overworld != null) {
                Supplier<T> supp = () -> {
                    try {
                        return clazz.getConstructor(String.class, SaveSync.class).newInstance(name, sync);
                    } catch (Exception e) {
                        return null;
                    }
                };
                return overworld.getDataStorage().computeIfAbsent(nbt -> {
                    T t = supp.get();
                    t.load(nbt);
                    return t;
                }, supp, name.replace(':', '_'));
            }
        }
        return null;
    }


    public static <T, S extends AbstractSyncSaveData> T execute(String name, Function<S, T> exec, Supplier<T> onError) {
        S instance = getInstance(name);
        if (instance != null)
            return exec.apply(instance);
        return onError.get();
    }

    public static <S extends AbstractSyncSaveData> void execute(String name, Consumer<S> exec) {
        S instance = getInstance(name);
        if (instance != null)
            exec.accept(instance);
    }

    @SubscribeEvent
    public static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide()) {
            for (String k : SAVE_DATAS.keySet()) {
                AbstractSyncSaveData d = getInstance(k);
                if (d != null)
                    NetworkManager.toClient(ModBase.COMMON_ID, (ServerPlayer) event.getPlayer(), d.pack(true));
            }
        } else
            CLIENT_INSTANCES.clear();
    }

    @SubscribeEvent
    public static void onPlayerLogoutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getPlayer().level.isClientSide())
            CLIENT_INSTANCES.clear();
    }
}
