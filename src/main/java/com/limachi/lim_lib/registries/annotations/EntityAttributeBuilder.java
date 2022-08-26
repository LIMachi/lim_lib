package com.limachi.lim_lib.registries.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * To be put on a static method that will return AttributeSupplier.Builder.
 * The name should match an entity registered with the same name
 * (leave default if in the same class as the registered entity, and this entity use a default name too).
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EntityAttributeBuilder {
    java.lang.String name() default "";
}
