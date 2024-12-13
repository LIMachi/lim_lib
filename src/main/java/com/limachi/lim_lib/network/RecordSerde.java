package com.limachi.lim_lib.network;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.World;
import com.limachi.lim_lib.reflection.Classes;
import com.limachi.lim_lib.reflection.Enums;
import com.limachi.lim_lib.registries.StaticInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * alternative to the Buffer class, precompile the serializers and deserializers of message in a modular way, allow extensions to be declared (should probably add a security to prevent multiple overrides of already declared serdes)
 */
@StaticInit
@SuppressWarnings("unchecked")
public class RecordSerde { //FIXME: add comprehensible errors on catch blocks
    @FunctionalInterface
    public interface ThrowingBiConsumer<P1, P2> {
        void accept(P1 p1, P2 p2) throws Exception;
    }

    @FunctionalInterface
    public interface RecordToBufferFragment<T extends Record> {
        ThrowingBiConsumer<T, FriendlyByteBuf> compile(Method fieldAccessor);
    }

    @FunctionalInterface
    public interface BufferToRecordFragment {
        ThrowingBiConsumer<FriendlyByteBuf, Object[]> compile(int index, RecordComponent field);
    }

    private static final HashMap<Class<?>, RecordToBufferFragment<?>> recordToBufferFragments = new HashMap<>();
    private static final HashMap<Class<?>, BufferToRecordFragment> bufferToRecordFragments = new HashMap<>();

    /**
     * make sure to use a static block/method annotated with @StaticInit to have it run before registration of messages!
     * example for boolean (notice the double declaration for both the object 'Boolean' and the primitive 'boolean'):
     * registerTypeConverter(Boolean.class, fieldAccessor->(buf, rec)->buf.writeBoolean((Boolean)fieldAccessor.invoke(rec)), index->(buf, parameters)->parameters[index] = buf.readBoolean());
     * registerTypeConverter(boolean.class, fieldAccessor->(buf, rec)->buf.writeBoolean((Boolean)fieldAccessor.invoke(rec)), index->(buf, parameters)->parameters[index] = buf.readBoolean());
     */
    public static void registerTypeConverter(Class<?> fieldType, RecordToBufferFragment<? extends Record> serializer, BufferToRecordFragment deserializer) {
        recordToBufferFragments.put(fieldType, serializer);
        bufferToRecordFragments.put(fieldType, deserializer);
    }

    static {
        registerTypeConverter(IBufferSerializable.class, f->(r, b)->((IBufferSerializable)f.invoke(r)).writeToBuff(b), (i, f)->(b, a)->{
            a[i] = Classes.newClass(f.getType());
            if (a[i] == null)
                throw new Exception();
            ((IBufferSerializable)a[i]).readFromBuff(b);
        });
        registerTypeConverter(Boolean.class, f->(r, b)->b.writeBoolean((Boolean)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readBoolean());
        registerTypeConverter(boolean.class, f->(r, b)->b.writeBoolean((Boolean)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readBoolean());
        registerTypeConverter(Byte.class, f->(r, b)->b.writeByte((Byte)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readByte());
        registerTypeConverter(byte.class, f->(r, b)->b.writeByte((Byte)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readByte());
        registerTypeConverter(Character.class, f->(r, b)->b.writeChar((Character)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readChar());
        registerTypeConverter(char.class, f->(r, b)->b.writeChar((Character)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readChar());
        registerTypeConverter(Short.class, f->(r, b)->b.writeShort((Short)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readShort());
        registerTypeConverter(short.class, f->(r, b)->b.writeShort((Short)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readShort());
        registerTypeConverter(Integer.class, f->(r, b)->b.writeInt((Integer)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readInt());
        registerTypeConverter(int.class, f->(r, b)->b.writeInt((Integer)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readInt());
        registerTypeConverter(Long.class, f->(r, b)->b.writeLong((Long)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readLong());
        registerTypeConverter(long.class, f->(r, b)->b.writeLong((Long)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readLong());
        registerTypeConverter(Float.class, f->(r, b)->b.writeFloat((Float)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readFloat());
        registerTypeConverter(float.class, f->(r, b)->b.writeFloat((Float)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readFloat());
        registerTypeConverter(Double.class, f->(r, b)->b.writeDouble((Double)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readDouble());
        registerTypeConverter(double.class, f->(r, b)->b.writeDouble((Double)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readDouble());
        registerTypeConverter(String.class, f->(r, b)->b.writeUtf((String)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readUtf());
        registerTypeConverter(CompoundTag.class, f->(r, b)->b.writeNbt((CompoundTag)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readAnySizeNbt());
        registerTypeConverter(BlockPos.class, f->(r, b)->b.writeBlockPos((BlockPos)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readBlockPos());
        registerTypeConverter(Component.class, f->(r, b)->b.writeComponent((Component)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readComponent());
        registerTypeConverter(BlockHitResult.class, f->(r, b)->b.writeBlockHitResult((BlockHitResult)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readBlockHitResult());
        registerTypeConverter(ChunkPos.class, f->(r, b)->b.writeChunkPos((ChunkPos)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readChunkPos());
        registerTypeConverter(ItemStack.class, f->(r, b)->b.writeItem((ItemStack) f.invoke(r)), (i, f)->(b, a)->a[i] = b.readItem());
        registerTypeConverter(FluidStack.class, f->(r, b)->b.writeFluidStack((FluidStack)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readFluidStack());
        registerTypeConverter(UUID.class, f->(r, b)->b.writeUUID((UUID)f.invoke(r)), (i, f)->(b, a)->a[i] = b.readUUID());
        registerTypeConverter(Enum.class, f->(r, b)->b.writeEnum((Enum<?>)f.invoke(r)), (i, f)->(b, a)->a[i] = Enums.anonymousEnumBuilder((Class<Enum<?>>)f.getType(), b.readVarInt()));
        registerTypeConverter(byte[].class, f->(r, b)->b.writeByteArray((byte[])f.invoke(r)), (i, f)->(b, a)->a[i] = b.readByteArray());
        registerTypeConverter(int[].class, f->(r, b)->b.writeVarIntArray((int[])f.invoke(r)), (i, f)->(b, a)->a[i] = b.readVarIntArray());
        registerTypeConverter(long[].class, f->(r, b)->b.writeLongArray((long[])f.invoke(r)), (i, f)->(b, a)->a[i] = b.readLongArray());
        registerTypeConverter(Vec3.class, f->(r, b)->{
            Vec3 v = (Vec3)f.invoke(r);
            b.writeDouble(v.x);
            b.writeDouble(v.y);
            b.writeDouble(v.z);
        }, (i, f)->(b, a)->a[i] = new Vec3(b.readDouble(), b.readDouble(), b.readDouble()));
        registerTypeConverter(Level.class, f->(r, b)->b.writeUtf(World.asString((Level)f.invoke(r))), (i, f)->(b, a)->a[i] = World.getLevel(b.readUtf()));
        registerTypeConverter(BlockPos[].class, f->(r, b)->{
            BlockPos[] v = (BlockPos[])f.invoke(r);
            b.writeInt(v.length);
            for (BlockPos t : v)
                b.writeBlockPos(t);
        }, (i, f)->(b, a)->{
            int size = b.readInt();
            BlockPos[] out = new BlockPos[size];
            for (int j = 0; j < size; ++j)
                out[j] = b.readBlockPos();
            a[i] = out;
        });
        registerTypeConverter(short[].class, f->(r, b)->{
            short[] v = (short[])f.invoke(r);
            b.writeInt(v.length);
            for (short t : v)
                b.writeShort(t);
        }, (i, f)->(b, a)->{
            int size = b.readInt();
            short[] out = new short[size];
            for (int j = 0; j < size; ++j)
                out[j] = b.readShort();
            a[i] = out;
        });
        registerTypeConverter(float[].class, f->(r, b)->{
            float[] v = (float[])f.invoke(r);
            b.writeInt(v.length);
            for (float t : v)
                b.writeFloat(t);
        }, (i, f)->(b, a)->{
            int size = b.readInt();
            float[] out = new float[size];
            for (int j = 0; j < size; ++j)
                out[j] = b.readFloat();
            a[i] = out;
        });
        registerTypeConverter(double[].class, f->(r, b)->{
            double[] v = (double[])f.invoke(r);
            b.writeInt(v.length);
            for (double t : v)
                b.writeDouble(t);
        }, (i, f)->(b, a)->{
            int size = b.readInt();
            double[] out = new double[size];
            for (int j = 0; j < size; ++j)
                out[j] = b.readDouble();
            a[i] = out;
        });
        registerTypeConverter(String[].class, f->(r, b)->{
            String[] v = (String[])f.invoke(r);
            b.writeInt(v.length);
            for (String t : v)
                b.writeUtf(t);
        }, (i, f)->(b, a)->{
            int size = b.readInt();
            String[] out = new String[size];
            for (int j = 0; j < size; ++j)
                out[j] = b.readUtf();
            a[i] = out;
        });
    }

    public static <T extends Record> BiConsumer<T, FriendlyByteBuf> compileRecordToBuffer(Class<T> record) {
        if (record.getRecordComponents().length == 0)
            return (r, b)->{};
        ThrowingBiConsumer<T, FriendlyByteBuf> tmp = (r, b)->{};
        boolean wrap = false;
        for (RecordComponent f : record.getRecordComponents()) {
            Class<?> ft = f.getType();
            final ThrowingBiConsumer<T, FriendlyByteBuf> prev = tmp;
            boolean matchFound = false;
            for (Map.Entry<Class<?>, RecordToBufferFragment<?>> e : recordToBufferFragments.entrySet())
                if (e.getKey().isAssignableFrom(ft)) {
                    if (wrap) {
                        final ThrowingBiConsumer<T, FriendlyByteBuf> finalConsumer = ((RecordToBufferFragment<T>) e.getValue()).compile(f.getAccessor());
                        tmp = (r, b) -> {
                            prev.accept(r, b);
                            finalConsumer.accept(r, b);
                        };
                    }
                    else
                        tmp = ((RecordToBufferFragment<T>)e.getValue()).compile(f.getAccessor());
                    wrap = true;
                    matchFound = true;
                }
            if (!matchFound) {
                Log.error(ft, "buffer conversion is not implemented for this object");
                System.exit(-1);
                return null;
            }
        }
        final ThrowingBiConsumer<T, FriendlyByteBuf> prev = tmp;
        return (r, b)->{
            try {
                prev.accept(r, b);
            } catch (Exception e) {
                Log.error(e.getStackTrace(), "Well, this is craptastic!");
                System.exit(-1);
            }
        };
    }

    public static <T extends Record> Function<FriendlyByteBuf, T> compileBufferToRecord(Class<T> record) {
        if (record.getRecordComponents().length == 0)
            return b-> {
                try {
                    return record.getConstructor().newInstance();
                } catch (Exception e) {
                    Log.error(e.getStackTrace(), "Can't access default constructor (might be private).");
                    System.exit(-1);
                    return null;
                }
            };
        ThrowingBiConsumer<FriendlyByteBuf, Object[]> tmp = (b, a)->{};
        RecordComponent[] rc = record.getRecordComponents();
        Class<?>[] types = new Class[rc.length];
        Constructor<T> constructor = null;
        for (int i = 0; i < rc.length; ++i)
            types[i] = rc[i].getType();
        try {
            constructor = record.getConstructor(types);
        } catch (Exception ignore) {}
        if (constructor == null)
            try {
                constructor = record.getDeclaredConstructor(types);
            } catch (Exception ignore) {}
        if (constructor == null) {
            Log.error(record, "Can't access default constructor using " + Arrays.toString(types) + "(might be private).");
            return null;
        }
        for (int i = 0; i < rc.length; ++i) {
            Class<?> ft = rc[i].getType();
            final ThrowingBiConsumer<FriendlyByteBuf, Object[]> prev = tmp;
            boolean matchFound = false;
            for (Map.Entry<Class<?>, BufferToRecordFragment> e : bufferToRecordFragments.entrySet())
                if (e.getKey().isAssignableFrom(ft)) {
                    if (i > 0) {
                        final ThrowingBiConsumer<FriendlyByteBuf, Object[]> finalConsumer = e.getValue().compile(i, rc[i]);
                        tmp = (b, a) -> {
                            prev.accept(b, a);
                            finalConsumer.accept(b, a);
                        };
                    } else
                        tmp = e.getValue().compile(i, rc[i]);
                    matchFound = true;
                }
            if (!matchFound) {
                Log.error(ft, "buffer conversion is not implemented for this object");
                System.exit(-1);
                return null;
            }
        }
        Constructor<T> finalConstructor = constructor;
        ThrowingBiConsumer<FriendlyByteBuf, Object[]> finalTmp = tmp;
        return b->{
            Object[] args = new Object[rc.length];
            try {
                finalTmp.accept(b, args);
                return finalConstructor.newInstance(args);
            } catch (Exception e) {
                Log.error(e.getStackTrace(), "Well, this is craptastic!");
                System.exit(-1);
            }
            return null;
        };
    }
}
