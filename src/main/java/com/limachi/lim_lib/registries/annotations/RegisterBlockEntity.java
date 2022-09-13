package com.limachi.lim_lib.registries.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Register this class block and set the annotated field to be a {@code RegistryObject<? extends Block>}
 * leave name to default if you want it to be generated from the class name
 * (will transform camel case names to snake case).
 * If you want to register an automatic BlockItem, use @RegisterBlockItem in the same class.
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterBlockEntity {
    String name() default "";
    String block() default "";
}
