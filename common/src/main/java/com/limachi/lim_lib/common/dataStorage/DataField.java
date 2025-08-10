package com.limachi.lim_lib.common.dataStorage;

import com.limachi.lim_lib.InstancedMod;
import com.limachi.lim_lib.common.annotations.RegisterData;
import com.limachi.lim_lib.common.annotations.RegisterEventListener;
import com.limachi.lim_lib.common.codec.Codecs;
import com.limachi.lim_lib.common.modCreation.Events;
import com.limachi.lim_lib.common.utils.Game;

import com.mojang.serialization.Codec;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Optional;

public class DataField<T> {
    protected static final HashMap<String, DataField<?>> fields = new HashMap<>(); //registry of all fields

    final protected T def;
    final protected Codec<T> codec;
    final protected HashMap<ResourceLocation, ValueFilePair> values = new HashMap<>();

    protected String id = null;
    protected RegisterData a = null;
    protected ResourceLocation defaultDim = null;

    protected class ValueFilePair {
        T value = def;
        final LevelDataFile file;

        protected ValueFilePair(String file, ResourceLocation dimension) {
            LevelDataFile[] tf = {null};
            if (a.saveToDisk()) {
                LevelDataFile.files.compute(file, (f, hrl)->{
                    if (hrl == null)
                        hrl = new HashMap<>();
                    hrl.compute(dimension, (r, ldf)->{
                        if (ldf == null)
                            ldf = new LevelDataFile(file, dimension);
                        if (!ldf.fields.containsKey(id))
                            ldf.fields.put(id, DataField.this);
                        tf[0] = ldf;
                        return ldf;
                    });
                    return hrl;
                });
            }
            this.file = tf[0];
        }

        protected ValueFilePair setValue(T value) { this.value = value; return this; }
    }

    public DataField(T value) {
        def = value;
        codec = (Codec<T>) Codecs.getCodec(value.getClass());
    }

    public DataField(T value, Codec<T> codec) {
        def = value;
        this.codec = codec;
    }

    public DataField<T> annotation(InstancedMod mod, RegisterData a, String id) {
        values.clear();
        this.a = a;
        this.id = id;
        String file = a.file();
        if (file.isBlank())
            file = mod.registries.mod_id;
        if (a.dimensions().length == 0 || (a.dimensions().length == 1 && a.dimensions()[0].isBlank())) {
            ResourceLocation t = Level.OVERWORLD.location();
            values.put(t, new ValueFilePair(file, t));
            defaultDim = t;
        } else
            for (String dim : a.dimensions()) {
                ResourceLocation t = ResourceLocation.parse(dim);
                values.put(t, new ValueFilePair(file, t));
                if (defaultDim == null)
                    defaultDim = t;
            }
        if (Game.isLogicalServer()) {
            for (ResourceLocation rl : values.keySet())
                if (Game.getLevel(rl) == null)
                    mod.logger.error("Invalid dimension id " + rl);
        }
        fields.put(id, this);
        return this;
    }

    public Optional<Tag> serialize(ResourceLocation level) {
        if (values.containsKey(level) && values.get(level) instanceof ValueFilePair p && p.value instanceof T t)
            return serialize(t);
        return Optional.empty();
    }

    protected Optional<Tag> serialize(T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value).result();
    }

    public void deserialize(Tag tag, ResourceLocation level) {
        if (values.containsKey(level))
            codec.decode(NbtOps.INSTANCE, tag).ifSuccess(t->values.computeIfPresent(level, (k, prev) -> prev.setValue(t.getFirst())));
    }

    protected void loadingCheck(ResourceLocation dimension) {
        if (a != null && a.saveToDisk() && values.get(dimension) instanceof ValueFilePair p && p.file instanceof LevelDataFile ldf)
            ldf.loadingCheck(Game.getLevel(dimension));
    }

    protected void loadingCheck(Level level) {
        ResourceLocation dimension = level.dimension().location();
        if (a != null && a.saveToDisk() && values.get(dimension) instanceof ValueFilePair p && p.file instanceof LevelDataFile ldf)
            ldf.loadingCheck(level);
    }

    public T get(Level level) {
        loadingCheck(level);
        ResourceLocation dimension = level.dimension().location();
        if (values.get(dimension) instanceof ValueFilePair p)
            return p.value;
        return def;
    }

    public T get(ResourceLocation dimension) {
        loadingCheck(dimension);
        if (values.get(dimension) instanceof ValueFilePair p)
            return p.value;
        return def;
    }

    public T get() {
        return get(Level.OVERWORLD.location());
    }

    public void set(Level level, T value) {
        loadingCheck(level);
        ResourceLocation dimension = level.dimension().location();
        values.computeIfPresent(dimension, (k, v)->{
            if (a != null && a.saveToDisk() && v.file instanceof LevelDataFile file)
                file.setDirty();
            if (a != null && a.syncToClient() && Game.isLogicalServer())
                sendSync(dimension, value);
            return v.setValue(value);
        });
    }

    public void set(ResourceLocation dimension, T value) {
        loadingCheck(dimension);
        values.computeIfPresent(dimension, (k, v)->{
            if (a != null && a.saveToDisk() && v.file instanceof LevelDataFile file)
                file.setDirty();
            if (a != null && a.syncToClient() && Game.isLogicalServer())
                sendSync(dimension, value);
            return v.setValue(value);
        });
    }

    public void set(T value) {
        set(Level.OVERWORLD.location(), value);
    }

    public void sendSync(ResourceLocation level, T value) {
        serialize(value).ifPresent(t->{
            CompoundTag tag =  new CompoundTag();
            tag.put(id, t);
            new SyncDataFieldMsg(level, tag).sendToClients();
        });
    }

    public void receiveSync(Tag tag, ResourceLocation level) {
        deserialize(tag, level);
    }

    public void invalidate() {
        values.replaceAll((i, v)->v.setValue(def));
    }

    public void sendAll(ServerPlayer player) {
        for (var p : values.entrySet()) {
            loadingCheck(p.getKey());
            serialize(p.getValue().value).ifPresent(t->{
                CompoundTag tag =  new CompoundTag();
                tag.put(id, t);
                new SyncDataFieldMsg(p.getKey(), tag).sendToClient(player);
            });
        }
    }

    @RegisterEventListener(Events.PLAYER_JOIN)
    public static void playerJoin(ServerPlayer player) {
        for (var f : fields.values())
            if (f.a instanceof RegisterData a && a.syncToClient())
                f.sendAll(player);
    }
}
