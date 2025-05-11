package com.limachi.lim_lib.common.annotations;

import com.limachi.lim_lib.common.modCreation.Loaders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * marker for classes that should only be loaded for specific loaders
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Loader {
    Loaders value();
}
