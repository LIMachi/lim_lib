package com.limachi.lim_lib.common.dataStorage;

import com.limachi.lim_lib.common.annotations.RegisterEventListener;
import com.limachi.lim_lib.common.modCreation.Events;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LevelDataFile extends SavedData {
    protected final String file;
    protected final ResourceLocation dimension;
    protected final HashMap<String, DataField<?>> fields = new HashMap<>();
    protected final Factory<LevelDataFile> FACTORY = new Factory<>(()->this, this::load, DataFixTypes.LEVEL);
    protected Level level = null;
    protected static final HashMap<String, HashMap<ResourceLocation, LevelDataFile>> files = new HashMap<>();

    public LevelDataFile(String file, ResourceLocation dimension) {
        this.file = file;
        this.dimension = dimension;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        if (level != null)
            for (var p : fields.entrySet())
                p.getValue().serialize(level.dimension().location()).ifPresent(t->compoundTag.put(p.getKey(), t));
        return compoundTag;
    }

    public void loadingCheck(Level level) {
        if (level instanceof ServerLevel sl && this.level == null && level.dimension().location().equals(dimension)) {
            this.level = level;
            sl.getDataStorage().computeIfAbsent(FACTORY, file);
        }
    }

    public LevelDataFile load(CompoundTag tag,  HolderLookup.Provider provider) {
        if (level != null)
            for (var p : fields.entrySet())
                p.getValue().deserialize(tag.get(p.getKey()), level.dimension().location());
        return this;
    }

    public void invalidate() { level = null; }

    @RegisterEventListener(Events.SERVER_STOPPING)
    public static void serverStopping(MinecraftServer state) {
        for (var h : files.values())
            for (var e : h.entrySet())
                e.getValue().invalidate();
        for (var f : DataField.fields.values())
            f.invalidate();
    }
}
