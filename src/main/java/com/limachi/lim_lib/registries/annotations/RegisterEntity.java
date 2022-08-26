package com.limachi.lim_lib.registries.annotations;

import net.minecraft.world.entity.MobCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * Register this class entity and set the annotated field to be a {@code RegistryObject<EntityType<? extends Entity>>}.
 * Leave name to default if you want it to be generated from the class name
 * (will transform camel case names to snake case).
 * Don't forget to declare the entity attributes, you can do so by using @EntityAttributeBuilder.
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RegisterEntity {
    String name() default "";
    MobCategory category() default MobCategory.MISC;
    float width() default 1f;
    float height() default 1f;
}
