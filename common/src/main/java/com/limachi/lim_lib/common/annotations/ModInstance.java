package com.limachi.lim_lib.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * populate the annotated field with your mod container so you can access the logger, registries, annotations, configs, etc...
 * this field is populated at the very beginning of mod creation and is guaranteed to be available if you followed the correct practices to use LimLib
 * (mainly, your fabric and neoforge entry point extend the correct classes and are annotated with @Mod/@FabricMod)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModInstance {
}
