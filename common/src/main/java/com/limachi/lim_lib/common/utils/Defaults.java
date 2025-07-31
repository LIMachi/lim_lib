package com.limachi.lim_lib.common.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class Defaults {
    public static final HashMap<Class<?>, Object> DEFAULTS = new HashMap<>();

    static {
        DEFAULTS.put(boolean.class, Boolean.FALSE);
        DEFAULTS.put(byte.class, (byte)0);
        DEFAULTS.put(short.class, (short)0);
        DEFAULTS.put(int.class, 0);
        DEFAULTS.put(long.class, 0L);
        DEFAULTS.put(float.class, 0F);
        DEFAULTS.put(double.class, 0.);
        DEFAULTS.put(char.class, '\0');

        DEFAULTS.put(Boolean.class, Boolean.FALSE);
        DEFAULTS.put(Byte.class, (byte)0);
        DEFAULTS.put(Short.class, (short)0);
        DEFAULTS.put(Integer.class, 0);
        DEFAULTS.put(Long.class, 0L);
        DEFAULTS.put(Float.class, 0F);
        DEFAULTS.put(Double.class, 0.);
        DEFAULTS.put(Character.class, '\0');

        DEFAULTS.put(String.class, "");

        DEFAULTS.put(Class.class, Void.class);

        DEFAULTS.put(ArrayList.class, new ArrayList<>());
        DEFAULTS.put(HashMap.class, new HashMap<>());
    }

    public static <T> T defaultOf(Class<T> type) {
        return (T) DEFAULTS.get(type);
    }
}
