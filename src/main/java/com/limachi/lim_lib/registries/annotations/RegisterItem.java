package com.limachi.lim_lib.registries.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Register this class item and set the annotated field to be a {@code RegistryObject<? extends Item>}.
 * Leave name to default if you want it to be generated from the class name
 * (will transform camel case names to snake case).
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterItem {
    java.lang.String name() default ""; //registry name of the object
    java.lang.String skip() default ""; //use this method path to make this registry optional (method should be static, of the format `boolean method(Class<?> annotation, String name)`, returning true if the object should be skipped, aka not registered)
    java.lang.String jeiInfoKey() default ""; //if set, will add an info tab in JEI for this item using the given translation key
}
