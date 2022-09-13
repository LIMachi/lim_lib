package com.limachi.lim_lib.reflection;

import java.util.HashMap;

public class Types {
    public static HashMap<Class<?>, Class<?>> CASTABLE_PRIMITIVES = new HashMap<>();
    protected static void castablePrimitive(Class<?> c1, Class<?> c2) {
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
    }

    protected static boolean canCast(Class<?> to, Class<?> from) {
        return to.isAssignableFrom(from) || CASTABLE_PRIMITIVES.getOrDefault(to, Void.class).isAssignableFrom(from) || to.isAssignableFrom(CASTABLE_PRIMITIVES.getOrDefault(from, Void.class));
    }

    protected static boolean paramTypesAreAssignable(Class<?>[] to, Class<?>[] from) {
        if (to.length != from.length) return false;
        for (int i = 0; i < to.length; ++i)
            if (!canCast(to[i], from[i])) //FIXME: does not work with primitives vs object of castable type (ex int/Integer)
                return false;
        return true;
    }
}
