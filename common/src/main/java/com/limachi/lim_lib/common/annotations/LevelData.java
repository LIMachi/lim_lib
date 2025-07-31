package com.limachi.lim_lib.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LevelData {
    String file() default  ""; //defaults to mod_id
    String[] dimensions() default  { "minecraft:overworld" };
    boolean saveToDisk() default true; //write to disk on world save
    boolean syncToClient() default false; //sync to client on change, on next world tick
}