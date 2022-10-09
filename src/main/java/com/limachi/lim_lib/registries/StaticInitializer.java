package com.limachi.lim_lib.registries;

import com.limachi.lim_lib.constructorEnforcer.ConstructorEnforcer;
import com.limachi.lim_lib.reflection.MethodHolder;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class StaticInitializer {

    private static boolean valueMatch(ModFileScanData.AnnotationData a, Stage stage) {
        return a.annotationData().containsKey("value") ? Enum.valueOf(Stage.class, ((ModAnnotation.EnumHolder)a.annotationData().get("value")).getValue()) == stage : stage == Stage.FIRST;
    }

    public static void initialize(String modId, Stage stage) {
        Type type = Type.getType(StaticInit.class);
        ModList.get().getAllScanData().stream()
                .filter(fsd -> fsd.getTargets().containsKey(modId))
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a->type.equals(a.annotationType()) && valueMatch(a, stage)).forEach(a -> {
            try {
                ConstructorEnforcer.testClass(Class.forName(a.clazz().getClassName()));
                if (a.targetType() == ElementType.METHOD) {
                    String name = a.memberName().replace("()V", "");
                    String skip = (String)a.annotationData().getOrDefault("skip", "");
                    if (skip.equals("") || !(boolean)MethodHolder.byPath(skip, null).invoke(StaticInit.class, name))
                        Class.forName(a.clazz().getClassName()).getMethod(name).invoke(null);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
                });
        if (stage == Stage.LAST)
            DistExecutor.unsafeRunForDist(()->()->{
                initializeClient(modId);
                return 0;}, ()->()->0);
    }

    private static void initializeClient(String modId) {
        Type type = Type.getType(StaticInitClient.class);
        ModList.get().getAllScanData().stream().filter(fsd -> fsd.getTargets().containsKey(modId)).map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a->type.equals(a.annotationType())).forEach(a -> {
            try {
                ConstructorEnforcer.testClass(Class.forName(a.clazz().getClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}
