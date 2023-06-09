package com.limachi.lim_lib.capabilities;

import com.limachi.lim_lib.Log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public interface ICopyCapOnDeath<T extends ICopyCapOnDeath<T>> {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface CopyOnDeath {}
    default void copy(T other) {
        autoCopy(this.getClass(), this, other.getClass(), other);
    }

    static void autoCopy(Class<?> tc, Object to, Class<?> fc, Object from) {
        if (tc != fc) return;
        Class<?> ztc = tc.getSuperclass();
        Class<?> zfc = fc.getSuperclass();
        if (ztc != null) autoCopy(ztc, to, zfc, from);
        Field[] fields = tc.getDeclaredFields();
        for (Field field : fields) {
            CopyOnDeath a = field.getAnnotation(CopyOnDeath.class);
            if (a != null) {
                try {
                    field.setAccessible(true);
                    field.set(to, field.get(from));
                } catch (Exception e) {
                    Log.error( "impossible to copy annotated field: " + field);
                }
            }
        }
    }
}
