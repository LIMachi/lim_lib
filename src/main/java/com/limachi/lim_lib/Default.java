package com.limachi.lim_lib;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.lang.reflect.Modifier;
import java.util.HashSet;

/**
 * <pre>
 * Marker interface to indicate that the implementing class should have a constructor with no parameters.
 * Will work like an annotation and will generate a crash if a class implement this but has no default constructor.
 * </pre>
 */
public interface Default {
    HashSet<Class<?>> ALREADY_TESTED = new HashSet<>();
    static void testDefault(Class<?> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers()) || ALREADY_TESTED.contains(clazz)) return;
        String name = Strings.getSimplifiedClassName(clazz.getName());
        if (Default.class.isAssignableFrom(clazz))
            try {
                clazz.getConstructor();
            } catch (NoSuchMethodException e) {
                Log.error(clazz, "Class is declared as default but has no valid constructor! I suggest adding a constructor of the form: `public " + name + "() {}` to this class.");
                e.printStackTrace();
                System.exit(-1);
            }
        ALREADY_TESTED.add(clazz);
    }
    static void testAllDefaults(String modId) {
        for (ModFileScanData scan : ModList.get().getAllScanData())
            if (scan.getTargets().containsKey(modId))
                for (ModFileScanData.ClassData cd : scan.getClasses()) {
                    String className = cd.clazz().getClassName();
                    if (className.contains("Mixin") || className.contains("mixin")) continue;
                    Class<?> clazz = Reflection.classByName(className, null);
                    if (clazz == null) {
                        System.exit(-1);
                        return;
                    }
                    testDefault(clazz);
                }
    }
}
