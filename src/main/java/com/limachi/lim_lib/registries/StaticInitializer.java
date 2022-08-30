package com.limachi.lim_lib.registries;

import com.limachi.lim_lib.constructorEnforcer.ConstructorEnforcer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class StaticInitializer {
    public static void initialize() {
        Type type = Type.getType(StaticInit.class);
        for (ModFileScanData.AnnotationData data : ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a->type.equals(a.annotationType())).collect(Collectors.toList())) {
            try {
                ConstructorEnforcer.testClass(Class.forName(data.clazz().getClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        DistExecutor.unsafeRunForDist(()->()->{
            initializeClient();
            return 0;}, ()->()->0);
    }

    private static void initializeClient() {
        Type type = Type.getType(StaticInitClient.class);
        for (ModFileScanData.AnnotationData data : ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a->type.equals(a.annotationType())).collect(Collectors.toList())) {
            try {
                ConstructorEnforcer.testClass(Class.forName(data.clazz().getClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
