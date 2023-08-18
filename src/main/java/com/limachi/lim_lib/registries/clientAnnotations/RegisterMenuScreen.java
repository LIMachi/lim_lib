package com.limachi.lim_lib.registries.clientAnnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterMenuScreen {
    String name() default ""; //registry name of the object
    String skip() default ""; //use this method path to make this registry optional (method should be static, of the format `boolean method(Class<?> annotation, String name)`, returning true if the object should be skipped, aka not registered)
    String menu() default ""; //registry name of the menu used to generate this screen, if not set will use the snake class name, swapping `_screen` for `_menu`)
}
