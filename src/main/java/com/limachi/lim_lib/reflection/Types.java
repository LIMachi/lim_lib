package com.limachi.lim_lib.reflection;

import java.util.HashMap;

@SuppressWarnings("unused")
public class Types {

    protected static HashMap<Class<?>, Class<?>> CASTABLE_PRIMITIVES = new HashMap<>();
    protected static HashMap<Class<?>, Object> DEFAULT_PRIMITIVES = new HashMap<>();

    private static void castablePrimitive(Class<?> c1, Class<?> c2) {
        CASTABLE_PRIMITIVES.put(c1, c2);
        CASTABLE_PRIMITIVES.put(c2, c1);
    }

    static {
        castablePrimitive(Boolean.class, boolean.class);
        castablePrimitive(Byte.class, byte.class);
        castablePrimitive(Character.class, char.class);
        castablePrimitive(Short.class, short.class);
        castablePrimitive(Integer.class, int.class);
        castablePrimitive(Long.class, long.class);
        castablePrimitive(Float.class, float.class);
        castablePrimitive(Double.class, double.class);

        DEFAULT_PRIMITIVES.put(boolean.class, false);
        DEFAULT_PRIMITIVES.put(byte.class, (byte)0);
        DEFAULT_PRIMITIVES.put(char.class, (char)0);
        DEFAULT_PRIMITIVES.put(short.class, (short)0);
        DEFAULT_PRIMITIVES.put(int.class, 0);
        DEFAULT_PRIMITIVES.put(long.class, 0L);
        DEFAULT_PRIMITIVES.put(float.class, 0f);
        DEFAULT_PRIMITIVES.put(double.class, 0d);
    }

    /**
     * test if the second class can be cast to the first, including primitive casts
     */
    public static boolean canCast(Class<?> to, Class<?> from) {
        return to.isAssignableFrom(from) || CASTABLE_PRIMITIVES.getOrDefault(to, Void.class).isAssignableFrom(from) || to.isAssignableFrom(CASTABLE_PRIMITIVES.getOrDefault(from, Void.class));
    }

    /**
     * if the given class is a primitive, return a default (false for boolean and 0 for all others), otherwise return null
     */
    public static Object getDefault(Class<?> clazz) { return DEFAULT_PRIMITIVES.getOrDefault(clazz, null); }

    /**
     * test if the two list are of same size and if the second list of classes can be cast to the first, including primitive casts
     */
    public static boolean paramTypesAreAssignable(Class<?>[] to, Class<?>[] from) {
        if (to.length != from.length) return false;
        for (int i = 0; i < to.length; ++i)
            if (!canCast(to[i], from[i]))
                return false;
        return true;
    }
}
