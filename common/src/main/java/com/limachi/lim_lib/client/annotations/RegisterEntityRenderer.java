package com.limachi.lim_lib.client.annotations;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Environment(EnvType.CLIENT)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisterEntityRenderer {
    String value() default ""; //target entity, defaults to simplified name() (ex: TestEntityRenderer -> TestEntity)
    String name() default ""; //entity renderer, defaults to class name
    //expects a constructor (EntityRendererProvider.Context)
}
