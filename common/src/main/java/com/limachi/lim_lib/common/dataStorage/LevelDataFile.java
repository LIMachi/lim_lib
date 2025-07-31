package com.limachi.lim_lib.common.dataStorage;

import com.limachi.lim_lib.InstancedMod;
import com.limachi.lim_lib.ModInstances;
import com.limachi.lim_lib.common.annotations.LevelData;
import com.limachi.lim_lib.common.reflect.FieldAccess;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;

public class LevelDataFile extends SavedData {

    protected final String file;
    protected final HashMap<String, LevelDataField<?>> fields = new HashMap<>();
    protected final Factory<LevelDataFile> FACTORY = new Factory<>(()->this, this::load, DataFixTypes.LEVEL);
    protected boolean loaded = false;

    public LevelDataFile(String file) {
        this.file = file;
    }

    public void addField(FieldAccess<?, ?> field, LevelData a, InstancedMod mod) {
        String path = field.clazz().toString() + "#" + field.name();
        if (field.get() instanceof LevelDataField<?> ldf)
            fields.put(path, ldf.annotation(a, this, mod));
        else
            mod.logger.error("Invalid field type '" + path + "' was " + field.get().getClass() + " expected LevelDataField");
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        for (var p : fields.entrySet())
            compoundTag.put(p.getKey(), p.getValue().serialize());
        return compoundTag;
    }

    public void loadingCheck(Level level) {
        if (level instanceof ServerLevel sl && !loaded) {
            sl.getDataStorage().computeIfAbsent(FACTORY, file);
            loaded = true;
        }
    }

    public LevelDataFile load(CompoundTag tag,  HolderLookup.Provider provider) {
        for (var p : fields.entrySet())
            p.getValue().deserialize(tag.getCompound(p.getKey()));
        return this;
    }
}
