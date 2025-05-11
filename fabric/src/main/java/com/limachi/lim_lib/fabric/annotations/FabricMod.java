package com.limachi.lim_lib.fabric.annotations;

import com.limachi.lim_lib.common.annotations.Loader;
import com.limachi.lim_lib.common.modCreation.Loaders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation that serves a similar purpose to @Mod from forge
 * (please put it on the fabric entry point that extends FabricEntryPoint)
 */
@Loader(Loaders.Fabric)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FabricMod {
    String value();
}
