package com.limachi.lim_lib.nbt;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings("unused")
public class NBT {

    public static CompoundTag clear(CompoundTag comp) {
        Object[] keys = comp.getAllKeys().toArray();
        for (Object key : keys)
            comp.remove((String)key);
        return comp;
    }

    protected static CompoundTag removeKeys(CompoundTag nbt, ListTag keys) {
        for (Tag k : keys) {
            if (k.getId() == Tag.TAG_COMPOUND) {
                String s = ((CompoundTag) k).getString("key");
                ListTag sk = (ListTag) ((CompoundTag) k).get("list");
                if (sk != null)
                    nbt.put(s, removeKeys(nbt.getCompound(s), sk));
            } else
                nbt.remove(k.getAsString());
        }
        return nbt;
    }

    protected static ListTag removedKeys(@Nonnull CompoundTag valid, @Nonnull CompoundTag diff) {
        ListTag list = new ListTag();
        for (String key : diff.getAllKeys())
            if (!valid.contains(key))
                list.add(StringTag.valueOf(key));
            else {
                Tag td = diff.get(key);
                Tag tv = valid.get(key);
                if (td instanceof CompoundTag && tv instanceof CompoundTag) {
                    ListTag sl = removedKeys((CompoundTag) tv, (CompoundTag) td);
                    if (!sl.isEmpty()) {
                        CompoundTag entry = new CompoundTag();
                        entry.put("list", sl);
                        entry.putString("key", key);
                        list.add(entry);
                    }
                } else
                    list.add(StringTag.valueOf(key));
            }
        return list;
    }

    public static CompoundTag extractDiff(@Nonnull CompoundTag valid, @Nonnull CompoundTag diff) {
        CompoundTag added = new CompoundTag();
        ListTag removed = new ListTag();
        CompoundTag changed = new CompoundTag();
        for (String key : valid.getAllKeys())
            if (diff.contains(key) && !diff.get(key).equals(valid.get(key))) {
                Tag tv = valid.get(key);
                Tag td = diff.get(key);
                if (tv instanceof CompoundTag && td instanceof CompoundTag)
                    changed.put(key, extractDiff((CompoundTag) tv, (CompoundTag) td));
                else if (tv != null)
                    changed.put(key, tv);
            }
            else if (!diff.contains(key)) {
                Tag t = valid.get(key);
                if (t != null)
                    added.put(key, t);
            }
        for (String key : diff.getAllKeys())
            if (!valid.contains(key))
                removed.add(StringTag.valueOf(key));
        CompoundTag out = new CompoundTag();
        if (!changed.isEmpty())
            out.put("Diff_Changed", changed);
        if (!added.isEmpty())
            out.put("Diff_Added", added);
        if (!removed.isEmpty())
            out.put("Diff_Removed", removed);
        if (!out.isEmpty())
            out.putBoolean("IsDiff", true);
        return out;
    }

    public static CompoundTag applyDiff(@Nonnull CompoundTag toChange, @Nonnull CompoundTag diff) {
        ListTag removed = diff.getList("Diff_Removed", Tag.TAG_LIST);
        for (int i = 0; i < removed.size(); ++i)
            toChange.remove(removed.getString(i));
        CompoundTag added = diff.getCompound("Diff_Added");
        for (String key : added.getAllKeys()) {
            Tag t = added.get(key);
            if (t != null)
                toChange.put(key, t);
        }
        CompoundTag changed = diff.getCompound("Diff_Changed");
        for (String key : changed.getAllKeys()) {
            Tag c = changed.get(key);
            if (toChange.contains(key) && c instanceof CompoundTag && toChange.get(key) instanceof CompoundTag) {
                Tag t = toChange.get(key);
                if (t != null)
                    toChange.put(key, applyDiff((CompoundTag) t, (CompoundTag) c));
            }
            else if (c != null)
                toChange.put(key, c);
        }
        return toChange;
    }

    public static CompoundTag ensurePathExistence(CompoundTag nbt, String ... nodes) {
        CompoundTag t = nbt;
        for (String node : nodes) {
            if (!t.contains(node))
                t.put(node, new CompoundTag());
            t = t.getCompound(node);
        }
        return t;
    }

    public static UUID readUUID(StringTag nbt) { return UUID.fromString(nbt.getAsString()); }
    public static StringTag writeUUID(UUID id) { return StringTag.valueOf(id.toString()); }

    protected static CompoundTag serializeAnnotations(Class<?> clazz, Object object, CompoundTag store) {
        if (clazz.isArray())
            return store;
        Class<?> zuper = clazz.getSuperclass();
        if (zuper != null) serializeAnnotations(zuper, object, store);
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
            } catch (Exception ignore) {
                continue;
            }
            TagSerialize a = field.getAnnotation(TagSerialize.class);
            if (a != null) {
                String name = a.name();
                if (name == null || name.isBlank())
                    name = Strings.camelToSnake(field.getName());
                Object fv = null;
                try {
                    fv = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (fv != null) {
                    if (field.getGenericType() instanceof ParameterizedType pt && (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType()) || field.getType().isArray())) {
                        Type[] ata = pt.getActualTypeArguments();
                        if (ata.length > 0) {
                            if (fv instanceof Map m) {
                                Class<?> t1 = null;
                                try {
                                    t1 = Class.forName(ata[0].getTypeName());
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                                if (t1 != null) {
                                    if (String.class.isAssignableFrom(t1) || m.isEmpty()) {
                                        CompoundTag out = new CompoundTag();
                                        for (Object k : m.keySet())
                                            if (k instanceof String ks)
                                                out.put(ks, serializeToTag(m.get(ks)));
                                        store.put(name, out);
                                    } else {
                                        ListTag out = new ListTag();
                                        for (Object tk : m.keySet()) {
                                            Tag k = serializeToTag(tk);
                                            if (k != null) {
                                                Tag v = serializeToTag(m.get(tk));
                                                CompoundTag e = new CompoundTag();
                                                e.put("key", k);
                                                e.put("value", v);
                                                out.add(e);
                                            }
                                        }
                                        store.put(name, out);
                                    }
                                }
                            } else if (fv instanceof Collection c) {
                                ListTag out = new ListTag();
                                for (Object o : c)
                                    out.add(serializeToTag(o));
                                store.put(name, out);
                            } else if (fv.getClass().isArray()) {
                                ListTag out = new ListTag();
                                for (Object o : (Object[])fv)
                                    out.add(serializeToTag(o));
                                store.put(name, out);
                            }
                        }
                    } else
                        store.put(name, serializeToTag(fv));
                }
            }
        }
        return store;
    }

    protected static boolean deserializeAnnotations(Class<?> clazz, Object object, CompoundTag tag) {
        if (clazz.isArray()) return false;
        Class<?> zuper = clazz.getSuperclass();
        if (zuper != null) deserializeAnnotations(zuper, object, tag);
        Field[] fields = clazz.getDeclaredFields();
        boolean success = false;
        for (Field field : fields) {
            try {
                field.setAccessible(true);
            } catch (Exception ignore) {
                continue;
            }
            TagSerialize a = field.getAnnotation(TagSerialize.class);
            if (a != null) {
                String name = a.name();
                if (name == null || name.isBlank())
                    name = Strings.camelToSnake(field.getName());
                Object out = null;
                if (field.getGenericType() instanceof ParameterizedType pt && (Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType()) || field.getType().isArray())) {
                    Type[] ata = pt.getActualTypeArguments();
                    if (ata.length > 0) {
                        Class<?> t1 = null;
                        Class<?> t2 = null;
                        try {
                            t1 = Class.forName(ata[0].getTypeName());
                            if (ata.length > 1)
                                t2 = Class.forName(ata[1].getTypeName());
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (Map.class.isAssignableFrom(field.getType()) && t1 != null && t2 != null) {
                            Map m;
                            try {
                                m = (Map)field.get(object);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                                continue;
                            }
                            m.clear();
                            Tag tm = tag.get(name);
                            if (String.class.isAssignableFrom(t1) && tm instanceof CompoundTag ctm)
                                for (String k : ctm.getAllKeys())
                                    m.put(k, deserializeFromTag(t2, null, ctm.get(k)));
                            else if (tm instanceof ListTag ltm) {
                                for (Tag le : ltm)
                                    if (le instanceof CompoundTag e)
                                        m.put(deserializeFromTag(t1, null, e.get("key")), deserializeFromTag(t2, null, e.get("value")));
                            }
                            out = m;
                        } else if (object instanceof Collection c && t1 != null) {
                            c.clear();
                            if (tag.get(name) instanceof ListTag l)
                                for (Tag e : l)
                                    c.add(deserializeFromTag(t1, null, e));
                            out = c;
                        } else if (object.getClass().isArray() && t1 != null) {
                            Object[] c = (Object[])object;
                            if (tag.get(name) instanceof ListTag l)
                                for (int i = 0; i < c.length; ++i) {
                                    Tag u = l.get(i);
                                    if (u != null)
                                        c[i] = deserializeFromTag(t1, null, u);
                                }

                        }
                    }
                } else
                    out = deserializeFromTag(field.getType(), null, tag.get(name));
                try {
                    field.set(object, out);
                    success = true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    /**
     * is not guaranteed to actually change the given object, use an assignation of the return instead
     */
    public static <T> T deserializeFromTag(T object, Tag tag) {
        if (object == null) return null;
        return deserializeFromTag((Class<T>)object.getClass(), object, tag);
    }

    public static <T extends Enum<T>> T deserializeFromTag(Class<T> clazz, T object, Tag tag) {
        if (tag instanceof StringTag t)
            return Enum.valueOf(clazz, t.getAsString());
        return null;
    }

    /**
     * is not guaranteed to actually change the given object, use an assignation of the return instead
     */
    public static <T> T deserializeFromTag(Class<T> clazz, Object object, Tag tag) {
        if (tag == null)
            return (T)object;
        else if (tag instanceof CompoundTag ct && object != null && deserializeAnnotations(clazz, object, ct))
            return (T)object;
        else if (clazz.isAssignableFrom(tag.getClass()))
            return (T)tag;
        else if ((Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz)) && tag instanceof ByteTag t)
            return (T)(Boolean)t.equals(ByteTag.ONE);
        else if ((Byte.class.isAssignableFrom(clazz) || byte.class.isAssignableFrom(clazz)) && tag instanceof ByteTag t)
            return (T)(Byte)t.getAsByte();
        else if ((Character.class.isAssignableFrom(clazz) || char.class.isAssignableFrom(clazz)) && tag instanceof IntTag t)
            return (T)(Integer)t.getAsInt();
        else if ((Short.class.isAssignableFrom(clazz) || short.class.isAssignableFrom(clazz)) && tag instanceof ShortTag t)
            return (T)(Short)t.getAsShort();
        else if ((Integer.class.isAssignableFrom(clazz) || int.class.isAssignableFrom(clazz)) && tag instanceof IntTag t)
            return (T)(Integer)t.getAsInt();
        else if ((Long.class.isAssignableFrom(clazz) || long.class.isAssignableFrom(clazz)) && tag instanceof LongTag t)
            return (T)(Long)t.getAsLong();
        else if ((Float.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) && tag instanceof FloatTag t)
            return (T)(Float)t.getAsFloat();
        else if ((Double.class.isAssignableFrom(clazz) || double.class.isAssignableFrom(clazz)) && tag instanceof DoubleTag t)
            return (T)(Double)t.getAsDouble();
        else if (String.class.isAssignableFrom(clazz) && tag instanceof StringTag t)
            return (T)t.getAsString();
        else if (BitSet.class.isAssignableFrom(clazz) && tag instanceof LongArrayTag t)
            return (T)BitSet.valueOf(t.getAsLongArray());
        else if (BlockPos.class.isAssignableFrom(clazz) && tag instanceof LongTag t)
            return (T)BlockPos.of(t.getAsLong());
        else if (BlockHitResult.class.isAssignableFrom(clazz)) {
            Log.error("BlockHitResult not implemented yet");
            return (T)object;
        } else if (Component.class.isAssignableFrom(clazz) && tag instanceof StringTag t)
            return (T)Component.Serializer.fromJson(t.getAsString());
        else if (ChunkPos.class.isAssignableFrom(clazz) && tag instanceof LongTag t)
            return (T)new ChunkPos(t.getAsLong());
        else if (Date.class.isAssignableFrom(clazz) && tag instanceof LongTag t)
            return (T)new Date(t.getAsLong());
        else if (ItemStack.class.isAssignableFrom(clazz) && tag instanceof CompoundTag t)
            return (T)ItemStack.of(t);
        else if (ResourceLocation.class.isAssignableFrom(clazz) && tag instanceof StringTag t)
            return (T)new ResourceLocation(t.getAsString());
        else if (FluidStack.class.isAssignableFrom(clazz) && tag instanceof CompoundTag t)
            return (T)FluidStack.loadFluidStackFromNBT(t);
        else if (UUID.class.isAssignableFrom(clazz) && tag instanceof StringTag t)
            return (T)readUUID(t);
        Log.error("missing deserializer for tag: " + tag + " (target class: " + clazz + ")");
        return (T)object;
    }

    public static Tag serializeToTag(Object object) {
        if (object == null) return EndTag.INSTANCE;
        if (object instanceof Tag)
            return (Tag)object;
        CompoundTag test = new CompoundTag();
        serializeAnnotations(object.getClass(), object, test);
        if (!test.isEmpty())
            return test;
        else if (object instanceof Boolean o)
            return o ? ByteTag.ONE : ByteTag.ZERO;
        else if (object instanceof Byte o)
            return ByteTag.valueOf(o);
        else if (object instanceof Character o)
            return IntTag.valueOf(o);
        else if (object instanceof Short o)
            return ShortTag.valueOf(o);
        else if (object instanceof Integer o)
            return IntTag.valueOf(o);
        else if (object instanceof Long o)
            return LongTag.valueOf(o);
        else if (object instanceof Float o)
            return FloatTag.valueOf(o);
        else if (object instanceof Double o)
            return DoubleTag.valueOf(o);
        else if (object instanceof String o)
            return StringTag.valueOf(o);
        else if (object instanceof BitSet o)
            return new LongArrayTag(o.toLongArray());
        else if (object instanceof BlockPos o)
            return LongTag.valueOf(o.asLong());
        else if (object instanceof BlockHitResult o) {
            Log.error("BlockHitResult not implemented yet");
            return null;
        }
        else if (object instanceof Component o)
            return StringTag.valueOf(Component.Serializer.toJson(o));
        else if (object instanceof ChunkPos o)
            return LongTag.valueOf(o.toLong()); //could have used a compound for better readability
        else if (object instanceof Date o)
            return LongTag.valueOf(o.getTime());
        else if (object instanceof ItemStack o)
            return o.serializeNBT();
        else if (object instanceof ResourceLocation o)
            return StringTag.valueOf(o.toString());
        else if (object instanceof FluidStack o)
            return o.writeToNBT(new CompoundTag());
        else if (object instanceof UUID o)
            return writeUUID(o);
        else if (object instanceof Enum o)
            return StringTag.valueOf(o.name()); //could have used ordinal for better compression, but opted for the readability of name instead
        Log.error("missing Tag serializer for object: " + object);
        return null;
    }
}
