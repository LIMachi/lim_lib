package com.limachi.lim_lib.network;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.reflection.Classes;
import com.limachi.lim_lib.reflection.Enums;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.fluids.FluidStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import java.util.BitSet;
import java.util.Date;
import java.util.UUID;

/**
 * helper class to manipulate FriendlyByteBuf (the buffers provided by forge)
 */
@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue", "ConstantConditions"})
public class Buffer {
    /**
     * Create a buffer on the heap (memory allocation). Intended to only be used in testing.
     */
    public static FriendlyByteBuf heapBuffer() { return new FriendlyByteBuf(Unpooled.buffer()); }

    /**
     * <pre>
     * Write a record to a buffer, reading this buffer with recordFromBuffer should result in the same record.
     * Supported record constructor parameters (other parameters will be skipped, resulting in a malformed buffer):
     * Boolean, boolean, Byte, byte, Character, char, Short, short, Integer, int, Long, long, Float, float,
     * Double, double, String, CompoundTag, BitSet, BlockPos, BlockHitResult, Component, ChunkPos, Date,
     * ItemStack, ResourceLocation, FluidStack, UUID, Enum, IBufferSerializable, Record
     * </pre>
     */
    public static <T extends Record> FriendlyByteBuf recordToBuffer(T record, FriendlyByteBuf buffer) {
        Field[] f = record.getClass().getDeclaredFields();
        for (Field field : f) {
            Class<?> ft = field.getType();
            Object fv;
            try {
                field.setAccessible(true);
                fv = field.get(record);
            } catch (Exception e) {
                //FIXME: add explicit error
                return buffer;
            }
            if (Boolean.class.isAssignableFrom(ft) || boolean.class.isAssignableFrom(ft))
                buffer.writeBoolean((Boolean) fv);
            else if (Byte.class.isAssignableFrom(ft) || byte.class.isAssignableFrom(ft))
                buffer.writeByte((Integer) fv);
            else if (Character.class.isAssignableFrom(ft) || char.class.isAssignableFrom(ft))
                buffer.writeChar((Integer) fv);
            else if (Short.class.isAssignableFrom(ft) || short.class.isAssignableFrom(ft))
                buffer.writeShort((Integer) fv);
            else if (Integer.class.isAssignableFrom(ft) || int.class.isAssignableFrom(ft))
                buffer.writeInt((Integer) fv);
            else if (Long.class.isAssignableFrom(ft) || long.class.isAssignableFrom(ft))
                buffer.writeLong((Long) fv);
            else if (Float.class.isAssignableFrom(ft) || float.class.isAssignableFrom(ft))
                buffer.writeFloat((Float) fv);
            else if (Double.class.isAssignableFrom(ft) || double.class.isAssignableFrom(ft))
                buffer.writeDouble((Double) fv);
            else if (String.class.isAssignableFrom(ft))
                buffer.writeUtf((String) fv);
            else if (CompoundTag.class.isAssignableFrom(ft))
                buffer.writeNbt((CompoundTag) fv);
            else if (BitSet.class.isAssignableFrom(ft))
                buffer.writeBitSet((BitSet) fv);
            else if (BlockPos.class.isAssignableFrom(ft))
                buffer.writeBlockPos((BlockPos) fv);
            else if (BlockHitResult.class.isAssignableFrom(ft))
                buffer.writeBlockHitResult((BlockHitResult) fv);
            else if (Component.class.isAssignableFrom(ft))
                buffer.writeComponent((Component) fv);
            else if (ChunkPos.class.isAssignableFrom(ft))
                buffer.writeChunkPos((ChunkPos) fv);
            else if (Date.class.isAssignableFrom(ft))
                buffer.writeDate((Date) fv);
            else if (ItemStack.class.isAssignableFrom(ft))
                buffer.writeItem((ItemStack) fv);
            else if (ResourceLocation.class.isAssignableFrom(ft))
                buffer.writeResourceLocation((ResourceLocation) fv);
            else if (FluidStack.class.isAssignableFrom(ft))
                buffer.writeFluidStack((FluidStack) fv);
            else if (UUID.class.isAssignableFrom(ft))
                buffer.writeUUID((UUID) fv);
            else if (Enum.class.isAssignableFrom(ft))
                buffer.writeEnum((Enum<?>) fv);
            else if (IBufferSerializable.class.isAssignableFrom(ft))
                ((IBufferSerializable) fv).writeToBuff(buffer);
            else if (Record.class.isAssignableFrom(ft))
                recordToBuffer((Record) fv, buffer);
            else
                Log.error(ft, "buffer conversion is not implemented for this object");
        }
        return buffer;
    }

    /**
     * <pre>
     * Convert a buffer to a record by calling the constructor of the record with read parameters from the buffer.
     * Expect the buffer to have been formatted by recordToBuffer.
     * Supported record constructor parameters (other parameters will be skipped, resulting in a malformed buffer):
     * Boolean, boolean, Byte, byte, Character, char, Short, short, Integer, int, Long, long, Float, float,
     * Double, double, String, CompoundTag, BitSet, BlockPos, BlockHitResult, Component, ChunkPos, Date,
     * ItemStack, ResourceLocation, FluidStack, UUID, Enum, IBufferSerializable, Record
     * </pre>
     */
    public static <T extends Record> T recordFromBuffer(Class<T> record, FriendlyByteBuf buffer) {
        Constructor<T> constructor = (Constructor<T>)record.getConstructors()[0];
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; ++i)
            if (Boolean.class.isAssignableFrom(paramTypes[i]) || boolean.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readBoolean();
            else if (Byte.class.isAssignableFrom(paramTypes[i]) || byte.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readByte();
            else if (Character.class.isAssignableFrom(paramTypes[i]) || char.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readChar();
            else if (Short.class.isAssignableFrom(paramTypes[i]) || short.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readShort();
            else if (Integer.class.isAssignableFrom(paramTypes[i]) || int.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readInt();
            else if (Long.class.isAssignableFrom(paramTypes[i]) || long.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readLong();
            else if (Float.class.isAssignableFrom(paramTypes[i]) || float.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readFloat();
            else if (Double.class.isAssignableFrom(paramTypes[i]) || double.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readDouble();
            else if (String.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readUtf();
            else if (CompoundTag.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readAnySizeNbt();
            else if (BitSet.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readBitSet();
            else if (BlockPos.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readBlockPos();
            else if (BlockHitResult.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readBlockHitResult();
            else if (Component.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readComponent();
            else if (ChunkPos.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readChunkPos();
            else if (Date.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readDate();
            else if (ItemStack.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readItem();
            else if (ResourceLocation.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readResourceLocation();
            else if (FluidStack.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readFluidStack();
            else if (UUID.class.isAssignableFrom(paramTypes[i]))
                params[i] = buffer.readUUID();
            else if (Enum.class.isAssignableFrom(paramTypes[i]))
                params[i] = Enums.anonymousEnumBuilder((Class<Enum<?>>)paramTypes[i], buffer.readVarInt());
            else if (IBufferSerializable.class.isAssignableFrom(paramTypes[i])) {
                params[i] = Classes.newClass(paramTypes[i]);
                ((IBufferSerializable)params[i]).readFromBuff(buffer);
            }
            else if (Record.class.isAssignableFrom(paramTypes[i]))
                params[i] = recordFromBuffer((Class<Record>)paramTypes[i], buffer);
            else {
                Log.error(paramTypes[i], "buffer conversion is not implemented for this object");
                return null;
            }
        try {
            return constructor.newInstance(params);
        } catch (Exception e) {
            //FIXME: add explicit error
            return null;
        }
    }
}
