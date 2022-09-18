package com.limachi.lim_lib.reflection;

import com.limachi.lim_lib.Log;

@SuppressWarnings("unused")
public class Enums {
    /**
     * <pre>
     * Allow construction of an enum from an object cast to an anonymous enum (we know that object is an enum, and we
     * know that we can store the instance, but we can't qualify the class entirely, used in method reflections)
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T anonymousEnumBuilder(Class<T> clazz, int ordinal) {
        if (clazz.isEnum()) {
            try {
                return ((T[])clazz.getMethod("values").invoke(null))[ordinal];
            } catch (Exception ignored) {}
        }
        Log.error(clazz, 1, "anonymousEnumBuilder -> Could not create instance of Enum class with ordinal: " + ordinal);
        return null;
    }
}
