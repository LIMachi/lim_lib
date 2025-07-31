package com.limachi.lim_lib.common.dataStorage;

import com.limachi.lim_lib.InstancedMod;
import com.limachi.lim_lib.ModInstances;
import com.limachi.lim_lib.common.annotations.LevelData;
import com.limachi.lim_lib.common.codec.Codecs;
import com.limachi.lim_lib.common.utils.Game;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Objects;

public class LevelDataField<T> {
    final T def;
    final HashMap<ResourceLocation, T> values = new HashMap<>();
    final Codec<T> codec;

    boolean saveToDisk = false;
    boolean syncToClient = false;
    LevelDataFile file = null;

    public LevelDataField(T value) {
        def = value;
        codec = (Codec<T>) Codecs.getCodec(value.getClass());
    }

    public LevelDataField(T value, Codec<T> codec) {
        def = value;
        this.codec = codec;
    }

    protected LevelDataField<T> annotation(LevelData a, LevelDataFile file, InstancedMod mod) {
        values.clear();
        for (String dim : a.dimensions())
            values.put(ResourceLocation.parse(dim), def);
        if (Game.isLogicalServer()) {
            for (ResourceLocation rl : values.keySet())
                if (Game.getLevel(rl) == null)
                    mod.logger.error("Invalid dimension id " + rl);
        }
        saveToDisk = a.saveToDisk();
        syncToClient = a.syncToClient();
        this.file = file;
        return this;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        for (var p : values.entrySet())
            codec.encodeStart(NbtOps.INSTANCE, p.getValue()).ifSuccess(t->tag.put(p.getKey().toString(), t));
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        for (var p : values.entrySet())
            codec.decode(NbtOps.INSTANCE, tag.get(p.getKey().toString())).ifSuccess(t->p.setValue(t.getFirst()));
    }

    protected void loadingCheck(Level level) {
        if (file != null && values.keySet().contains(level.dimension().location()))
            file.loadingCheck(level);
    }

    public T get(Level level) {
        loadingCheck(level);
        return values.get(level.dimension().location());
    }

    public void set(Level level, T value) {
        loadingCheck(level);
        values.computeIfPresent(level.dimension().location(), (k, v)->{
            if (saveToDisk)
                file.setDirty();
            if (syncToClient && Game.isLogicalServer() && !Objects.equals(v, value))
                ;//do sync
            return value;
        });
    }
}
