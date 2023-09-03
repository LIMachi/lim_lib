package com.limachi.lim_lib.widgetsOldOld.containerData;

import com.limachi.lim_lib.Log;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.ContainerData;

import java.util.UUID;

/**
 * <pre>
 * Helper implementation of ContainerData to simplify the encoding of String, Float, Double, etc...
 * The functions using int are labeled TrueInt because despite declaring get and set as using ints,
 * the message are actually encoded in shorts when sent to the client, losing data when going above Short.MAX_VALUE!
 * Using getShort and setShort is actually the same as DataSlot#get and DataSlot#set
 * Support for other types might be added latter.
 * </pre>
 */
@SuppressWarnings("unused")
public class DataSlots implements ContainerData {

    private final NonNullList<Short> encoded;

    /**
     * in reality extra is mostly used to differentiate this Constructor from the Int constructor.
     */
    protected DataSlots(int maxSize, boolean extra) {
        if (extra)
            ++maxSize;
        if (maxSize >= Short.MAX_VALUE) {
            Log.error("Trying to create a FriendlyContainerData with size above short limit! " + maxSize);
            maxSize = Short.MAX_VALUE - 1;
        }
        encoded = NonNullList.withSize(maxSize, (short)0);
    }

    public DataSlots(int maxCharacters, String initialString) {
        this(maxCharacters, true);
        setString(initialString);
    }

    public DataSlots(int initialTrueInt) {
        this(2, false);
        setTrueInt(initialTrueInt);
    }

    public DataSlots(float initialFloat) {
        this(2, false);
        setFloat(initialFloat);
    }

    public DataSlots(double initialDouble) {
        this(4, false);
        setDouble(initialDouble);
    }

    public DataSlots(short initialShort) {
        this(1, false);
        setShort(initialShort);
    }

    public DataSlots(char initialChar) {
        this(1, false);
        setChar(initialChar);
    }

    public DataSlots(long initialLong) {
        this(4, false);
        setLong(initialLong);
    }

    public DataSlots(UUID initialUUID) {
        this(8, false);
        setUUID(initialUUID);
    }

    public double getDouble() {
        if (encoded.size() < 4) return 0.;
        return Double.longBitsToDouble((((long)encoded.get(0)) << 48) | (((long)encoded.get(1)) << 32) | (((long)encoded.get(2)) << 16) | (long)encoded.get(3));
    }

    public void setDouble(double value) {
        if (encoded.size() < 4) return;
        long t = Double.doubleToRawLongBits(value);
        encoded.set(0, (short)((t >> 48) & 0xFFFF));
        encoded.set(1, (short)((t >> 32) & 0xFFFF));
        encoded.set(2, (short)((t >> 16) & 0xFFFF));
        encoded.set(3, (short)(t & 0xFFFF));
    }

    public float getFloat() {
        if (encoded.size() < 2) return 0f;
        return Float.intBitsToFloat(((int)encoded.get(0)) << 16 | (int)encoded.get(1));
    }

    public void setFloat(float value) {
        if (encoded.size() < 2) return;
        int t = Float.floatToRawIntBits(value);
        encoded.set(0, (short)((t >> 16) & 0xFFFF));
        encoded.set(1, (short)(t & 0xFFFF));
    }

    public int getTrueInt() {
        if (encoded.size() < 2) return 0;
        return ((int)encoded.get(0) & 0xFFFF) << 16 | ((int)encoded.get(1) & 0xFFFF);
    }

    public void setTrueInt(int value) {
        if (encoded.size() < 2) return;
        encoded.set(0, (short)((value >> 16) & 0xFFFF));
        encoded.set(1, (short)(value & 0xFFFF));
    }

    public String getString() {
        if (encoded.size() < 1 || encoded.get(0) >= encoded.size()) return "";
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < encoded.get(0); ++i)
            out.append((char)(short)encoded.get(i + 1));
        return out.toString();
    }

    public void setString(String value) {
        int max = Integer.min(value.length(), encoded.size() - 1);
        encoded.set(0, (short)max);
        for (int i = 0; i < max; ++i)
            encoded.set(1 + i, (short)value.charAt(i));
    }

    public short getShort() {
        if (encoded.size() < 1) return 0;
        return encoded.get(0);
    }

    public void setShort(short value) {
        if (encoded.size() < 1) return;
        encoded.set(0, value);
    }

    public char getChar() {
        if (encoded.size() < 1) return 0;
        return (char)(short)encoded.get(0);
    }

    public void setChar(char value) {
        if (encoded.size() < 1) return;
        encoded.set(0, (short)value);
    }

    public long getLong() {
        if (encoded.size() < 4) return 0;
        return (((long)encoded.get(0) & 0xFFFF) << 48) | (((long)encoded.get(1) & 0xFFFF) << 32) | (((long)encoded.get(2) & 0xFFFF) << 16) | ((long)encoded.get(3) & 0xFFFF);
    }

    public void setLong(long value) {
        if (encoded.size() < 4) return;
        encoded.set(0, (short)((value >> 48) & 0xFFFF));
        encoded.set(1, (short)((value >> 32) & 0xFFFF));
        encoded.set(2, (short)((value >> 16) & 0xFFFF));
        encoded.set(3, (short)(value & 0xFFFF));
    }

    public UUID getUUID() {
        if (encoded.size() < 8) return new UUID(0, 0);
        long high = (((long)encoded.get(0) & 0xFFFF) << 48) | (((long)encoded.get(1) & 0xFFFF) << 32) | (((long)encoded.get(2) & 0xFFFF) << 16) | ((long)encoded.get(3) & 0xFFFF);
        long low = (((long)encoded.get(4) & 0xFFFF) << 48) | (((long)encoded.get(5) & 0xFFFF) << 32) | (((long)encoded.get(6) & 0xFFFF) << 16) | ((long)encoded.get(7) & 0xFFFF);
        return new UUID(high, low);
    }

    public void setUUID(UUID value) {
        if (encoded.size() < 8) return;
        long high = value.getMostSignificantBits();
        long low = value.getLeastSignificantBits();
        encoded.set(0, (short)((high >> 48) & 0xFFFF));
        encoded.set(1, (short)((high >> 32) & 0xFFFF));
        encoded.set(2, (short)((high >> 16) & 0xFFFF));
        encoded.set(3, (short)(high & 0xFFFF));
        encoded.set(4, (short)((low >> 48) & 0xFFFF));
        encoded.set(5, (short)((low >> 32) & 0xFFFF));
        encoded.set(6, (short)((low >> 16) & 0xFFFF));
        encoded.set(7, (short)(low & 0xFFFF));
    }

    @Override
    public int get(int index) {
        return index >= 0 && index < encoded.size() ? encoded.get(index) : 0;
    }

    @Override
    public void set(int index, int value) {
        if (index >= 0 && index < encoded.size())
            encoded.set(index, (short)value);
    }

    @Override
    public int getCount() {
        return encoded.size();
    }
}
