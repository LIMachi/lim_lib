package com.limachi.lim_lib;

import org.objectweb.asm.ClassReader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ModInstances {
    private static final HashMap<String, InstancedMod> instances = new HashMap<>();
    private static final HashMap<Class<?>, InstancedMod> ownedClasses = new HashMap<>();
    private static final HashSet<String> alreadyOwned = new HashSet<>();

    public static InstancedMod newMod(Class<?> classForAccessor, String id, String rootPackage, Predicate<ClassReader> annotationFilter) {
        if (instances.containsKey(id))
            throw new IllegalArgumentException("Duplicate mod id: " + id);
        InstancedMod mod = new InstancedMod(classForAccessor, id, rootPackage, annotationFilter);
        instances.put(id, mod);
        return mod;
    }

    public static InstancedMod getMod(String id) {
        return instances.get(id);
    }

    public static Set<String> getAllModIds() {
        return instances.keySet();
    }

    public static boolean runOnMod(String id, Consumer<InstancedMod> run) {
        if (getMod(id) instanceof InstancedMod mod) {
            run.accept(mod);
            return true;
        }
        return false;
    }

    public static <T> T runOnMod(String id, Function<InstancedMod, T> run, Supplier<T> orElse) {
        if (getMod(id) instanceof InstancedMod mod)
            return run.apply(mod);
        return orElse.get();
    }

    public static boolean runOnMod(String id, Consumer<InstancedMod> run, Runnable orElse) {
        if (getMod(id) instanceof InstancedMod mod) {
            run.accept(mod);
            return true;
        }
        orElse.run();
        return false;
    }

    public static InstancedMod getModByOwnedClass(Class<?> clazz) {
        if (!ownedClasses.containsKey(clazz))
            for (var e : instances.entrySet()) {
                if (alreadyOwned.contains(e.getKey()))
                    continue;
                for (Class<?> c : e.getValue().extractor.extractedClasses())
                    ownedClasses.put(c, e.getValue());
                alreadyOwned.add(e.getKey());
            }
        return ownedClasses.get(clazz);
    }
}
