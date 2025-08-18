package com.limachi.lim_lib.common.codec;

import com.limachi.lim_lib.common.reflect.ReflectUtils;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CodecUtils {
    //TODO: test if this works
    public static <T extends Record> Codec<T> recordCodec() { return recordCodec(ReflectUtils.classOfGeneric()); }
    public static <T extends Record> Codec<T> recordCodec(Class<T> rec) {
        var comps = rec.getRecordComponents();
        if (comps == null)
            throw new RuntimeException("getRecordComponents returned null for " + rec);
        if (comps.length == 0 || comps.length > 16)
            throw new RuntimeException("invalid record length " + comps.length + " for " + rec);
        return RecordCodecBuilder.create(b->{
            var types = new Class[comps.length];
            var codecs = new RecordCodecBuilder[comps.length];
            for (int i = 0; i < comps.length; ++i) {
                types[i] = comps[i].getType();
                Codec<?> tc;
                if ((tc = autoCodec(types[i])) == null)
                    throw new RuntimeException("no codec found for type: " + types[i]);
                int finalI = i;
                Function<T, Object> get = o-> {
                    try {
                        return comps[finalI].getAccessor().invoke(o);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
                ((MapCodec<Object>)tc.fieldOf(comps[i].getName())).forGetter(get);
            }
            Constructor<T> n;
            try {
                n = rec.getConstructor(types);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            return switch (comps.length) {
                case 1 -> b.group(codecs[0]).apply(b, p-> ReflectUtils.nullableInstance(n, p));
                case 2 -> b.group(codecs[0], codecs[1]).apply(b, (p0, p1)-> ReflectUtils.nullableInstance(n, p0, p1));
                case 3 -> b.group(codecs[0], codecs[1], codecs[2]).apply(b, (p0, p1, p2)-> ReflectUtils.nullableInstance(n, p0, p1, p2));
                case 4 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3]).apply(b, (p0, p1, p2, p3)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3));
                case 5 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4]).apply(b, (p0, p1, p2, p3, p4)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4));
                case 6 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5]).apply(b, (p0, p1, p2, p3, p4, p5)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5));
                case 7 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6]).apply(b, (p0, p1, p2, p3, p4, p5, p6)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6));
                case 8 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7));
                case 9 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8));
                case 10 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8], codecs[9]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8, p9)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9));
                case 11 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8], codecs[9], codecs[10]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
                case 12 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8], codecs[9], codecs[10], codecs[11]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11));
                case 13 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8], codecs[9], codecs[10], codecs[11], codecs[12]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12));
                case 14 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8], codecs[9], codecs[10], codecs[11], codecs[12], codecs[13]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13));
                case 15 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8], codecs[9], codecs[10], codecs[11], codecs[12], codecs[13], codecs[14]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14));
                case 16 -> b.group(codecs[0], codecs[1], codecs[2], codecs[3], codecs[4], codecs[5], codecs[6], codecs[7], codecs[8], codecs[9], codecs[10], codecs[11], codecs[12], codecs[13], codecs[14], codecs[15]).apply(b, (p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)-> ReflectUtils.nullableInstance(n, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15));
                default -> throw new IllegalStateException("Unexpected value: " + comps.length);
            };
        });
    }

    //TODO: test if this works
    public static <T> Codec<T[]> arrayCodec() { return arrayCodec(ReflectUtils.classOfGeneric()); }
    public static <T> Codec<T[]> arrayCodec(Class<T[]> clazz) {
        final Codec<T> inner = (Codec<T>)autoCodec(clazz.componentType());
        return new PrimitiveCodec<>() {
            @Override
            public <D> DataResult<T[]> read(DynamicOps<D> ops, D input) {
                return ops.getStream(input).flatMap(s->{
                    List<D> list = s.toList();
                    T[] out = (T[]) Array.newInstance(clazz, list.size());
                    for (int i = 0; i < list.size(); ++i) {
                        DataResult<Pair<T, D>> t = inner.decode(ops, list.get(i));
                        if (t.isSuccess())
                            out[i] = t.result().get().getFirst();
                        else
                            return DataResult.error(t.error().get().messageSupplier());
                    }
                    return DataResult.success(out);
                });
            }

            @Override
            public <D> D write(DynamicOps<D> ops, T[] value) {
                return ops.createList(Arrays.stream(value).map(o->inner.encodeStart(ops, o).result().get()));
            }
        };
    }

    public static <T> Codec<T> autoCodec() { return autoCodec(ReflectUtils.classOfGeneric()); }
    public static <T> Codec<T> autoCodec(Class<T> clazz) {
        if (Record.class.isAssignableFrom(clazz))
            return (Codec<T>)recordCodec((Class<Record>) clazz);
        Codec<T> c = Codecs.getCodec(clazz);
        if (c == null && clazz.isArray())
            return (Codec<T>)arrayCodec();
        return c;
    }

    public static <B extends RegistryFriendlyByteBuf, T> T read(B buf) { return read(buf, ReflectUtils.classOfGeneric()); }
    public static <B extends RegistryFriendlyByteBuf, T> T read(B buf, Class<T> clazz) {
        return StreamCodecs.getCodec(clazz).decode(buf);
    }

    public static <B extends RegistryFriendlyByteBuf, T> B write(B buf, T obj) {
        StreamCodec<RegistryFriendlyByteBuf, T> codec = (StreamCodec<RegistryFriendlyByteBuf, T>) StreamCodecs.getCodec(obj.getClass());
        codec.encode(buf, obj);
        return buf;
    }

    public static <B extends RegistryFriendlyByteBuf, T extends Record> StreamCodec<B, T> recordStreamCodec() { return recordStreamCodec(ReflectUtils.classOfGeneric()); }
    public static <B extends RegistryFriendlyByteBuf, T extends Record> StreamCodec<B, T> recordStreamCodec(Class<T> rec) {
        var comps = rec.getRecordComponents();
        var types = new Class[comps.length];
        for (int i = 0; i < comps.length; ++i)
            types[i] = comps[i].getType();
        Constructor<T> n = ReflectUtils.nullableConstructor(rec, types);
        StreamDecoder<B, T> reader = b->{
            Object[] params = new Object[comps.length];
            for (int i = 0; i < comps.length; ++i)
                params[i] = read(b, types[i]);
            return ReflectUtils.nullableInstance(n, params);
        };
        StreamEncoder<B, T> writer = (b, t)->{
            for (var comp : comps)
                write(b, ReflectUtils.getComponent(comp, t));
        };
        return StreamCodec.of(writer, reader);
    }

    public static <B extends RegistryFriendlyByteBuf, T> StreamCodec<B, T> autoStreamCodec() { return autoStreamCodec(ReflectUtils.classOfGeneric()); }
    public static <B extends RegistryFriendlyByteBuf, T> StreamCodec<B, T> autoStreamCodec(Class<T> clazz) {
        if (Record.class.isAssignableFrom(clazz))
            return (StreamCodec<B, T>) recordStreamCodec((Class<Record>) clazz);
        return (StreamCodec<B, T>) StreamCodecs.getCodec(clazz);
    }

    /**
     * contrary to the vanilla codec, this function works with any map (including mutable ones, like HashMap, and maps that have non string keys)
     * works by making 2 ordered list of same size and inserting them as child of a single object
     */
    public static <K, V, M extends Map<K, V>> Codec<M> mapCodec(Supplier<M> mapConstructor, Codec<K> keyCodec, Codec<V> valueCodec) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<M, T>> decode(DynamicOps<T> ops, T input) {
                return ops.getMap(input).flatMap(m->{
                    var k = m.get("keys");
                    var v = m.get("values");
                    if (k != null && v != null) {
                        var lk = ops.getStream(k);
                        var lv = ops.getStream(v);
                        if (lk.isSuccess() && lv.isSuccess()) {
                            var lkr = lk.result();
                            var lvr = lv.result();
                            if (lkr.isPresent() && lvr.isPresent()) {
                                var uk = lkr.get().toList();
                                var uv = lvr.get().toList();
                                if (uk.size() == uv.size()) {
                                    var out = mapConstructor.get();
                                    for (int i = 0; i < uk.size(); ++i) {
                                        var tk = keyCodec.decode(ops, uk.get(i));
                                        var tv = valueCodec.decode(ops, uv.get(i));
                                        if (tk.isSuccess() && tv.isSuccess())
                                            out.put(tk.getOrThrow().getFirst(), tv.getOrThrow().getFirst());
                                    }
                                    return DataResult.success(Pair.of(out, input));
                                }
                            }
                        }
                    }
                    return DataResult.error(() -> "Keys and/or values are not same"); //should do a more detailed error
                });
            }

            @Override
            public <T> DataResult<T> encode(M input, DynamicOps<T> ops, T prefix) {
                var k = ops.listBuilder();
                var v = ops.listBuilder();
                for (var e : input.entrySet()) {
                    var tk = keyCodec.encodeStart(ops, e.getKey());
                    var tv = valueCodec.encodeStart(ops, e.getValue());
                    if (tk.isSuccess() && tv.isSuccess()) {
                        k.add(tk);
                        v.add(tv);
                    } else {
                        //merge and return errors
                    }
                }
                return ops
                        .mapBuilder()
                        .add("keys", k.build(prefix))
                        .add("values", v.build(prefix))
                        .build(prefix);
            }
        };
    }

    public static <K, V, M extends Map<K, V>> StreamCodec<RegistryFriendlyByteBuf, M> mapStreamCodec(Supplier<M> mapConstructor, StreamCodec<RegistryFriendlyByteBuf, K> keyCodec, StreamCodec<RegistryFriendlyByteBuf, V> valueCodec) {
        return StreamCodec.of((b, m)->{
            b.writeVarInt(m.size());
            for (var entry : m.entrySet()) {
                keyCodec.encode(b, entry.getKey());
                valueCodec.encode(b, entry.getValue());
            }
        }, b->{
            var out = mapConstructor.get();
            int len = b.readVarInt();
            for (int i = 0; i < len; ++i) {
                K k = keyCodec.decode(b);
                V v = valueCodec.decode(b);
                out.put(k, v);
            }
            return out;
        });
    }

    public static <K, V> Codec<Pair<K, V>> pairCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return RecordCodecBuilder.create(b->b.group(
                keyCodec.fieldOf("first").forGetter(Pair::getFirst),
                valueCodec.fieldOf("second").forGetter(Pair::getSecond)
        ).apply(b, Pair::of));
    }

    public static <K, V> StreamCodec<RegistryFriendlyByteBuf, Pair<K, V>> pairStreamCodec(StreamCodec<RegistryFriendlyByteBuf, K> keyCodec, StreamCodec<RegistryFriendlyByteBuf, V> valueCodec) {
        return StreamCodec.of((b, p)->{
            keyCodec.encode(b, p.getFirst());
            valueCodec.encode(b, p.getSecond());
        }, b->{
            K k = keyCodec.decode(b);
            V v = valueCodec.decode(b);
            return Pair.of(k, v);
        });
    }

    public static <V, C extends Collection<V>> Codec<C> collectionCodec(Supplier<C> collectionConstructor, Codec<V> valueCodec) {
        return new PrimitiveCodec<>() {
            @Override
            public <T> DataResult<C> read(DynamicOps<T> ops, T input) {
                return ops.getStream(input).flatMap(c->{
                    C out = collectionConstructor.get();
                    c.forEach(e-> out.add(valueCodec.decode(ops, e).getPartialOrThrow().getFirst()));
                    return DataResult.success(out);
                });
            }

            @Override
            public <T> T write(DynamicOps<T> ops, C value) {
                return ops.createList(value.stream().map(v->valueCodec.encodeStart(ops, v).getPartialOrThrow()));
            }
        };
    }

    public static <V, C extends Collection<V>> StreamCodec<RegistryFriendlyByteBuf, C> collectionStreamCodec(Supplier<C> collectionConstructor, StreamCodec<RegistryFriendlyByteBuf, V> valueCodec) {
        return StreamCodec.of((b, c)->{
            b.writeVarInt(c.size());
            for (var entry : c)
                valueCodec.encode(b, entry);
        }, b->{
            var out = collectionConstructor.get();
            int len = b.readVarInt();
            for (int i = 0; i < len; ++i)
                out.add(valueCodec.decode(b));
            return out;
        });
    }
}
