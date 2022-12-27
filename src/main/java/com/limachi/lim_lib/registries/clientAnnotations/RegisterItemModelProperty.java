package com.limachi.lim_lib.registries.clientAnnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterItemModelProperty {
    java.lang.String name() default ""; //by default will use the resource location: mod_id:method_name for the item this method is declared on
//    java.lang.String item() default "";
}
