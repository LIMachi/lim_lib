package com.limachi.lim_lib;

import com.limachi.lim_lib.client.modCreation.ClientRegistries;
import com.limachi.lim_lib.client.modCreation.ClientStage;
import com.limachi.lim_lib.common.annotations.ModInstance;
import com.limachi.lim_lib.common.annotations.PreRegistries;
import com.limachi.lim_lib.common.annotations.ReloadListener;
import com.limachi.lim_lib.common.commands.CommandManager;
import com.limachi.lim_lib.common.config.ConfigManager;
import com.limachi.lim_lib.common.modCreation.Registries;
import com.limachi.lim_lib.common.modCreation.Stage;
import com.limachi.lim_lib.common.modCreation.StaticInitializer;
import com.limachi.lim_lib.common.reflect.AnnotationExtractor;
import com.limachi.lim_lib.common.reflect.FieldAccess;
import com.limachi.lim_lib.common.scrollSystem.ScrollHandler;

import dev.architectury.registry.ReloadListenerRegistry;

import dev.architectury.utils.EnvExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class InstancedMod {
    public final Logger logger;
    public final Registries registries;
    public final AnnotationExtractor extractor;
    public final ConfigManager configs;
    public Object client = null;

    InstancedMod(Class<?> classForAccessor, String id, String rootPackage, Predicate<ClassReader> annotationFilter) {
        extractor = new AnnotationExtractor(classForAccessor, rootPackage.replace(".", "[/\\\\]") + "[/\\\\].*", annotationFilter);
        logger = LogManager.getLogger(id);
        extractor.runOnFields(ModInstance.class, (f, m)->{
            if (f.type() == InstancedMod.class)
                ((FieldAccess<?, InstancedMod>)f).set(this);
            else
                throw new IllegalArgumentException("Invalid field type for @ModInstance: " + f.type() + " (should be: com.limachi.lim_lib.InstancedMod)");
        });
        registries = new Registries(id, this);
        EnvExecutor.runInEnv(EnvType.CLIENT, ()->()->{
            client = new ClientRegistries(this);
        });
        configs = new ConfigManager(id, extractor);
        extractor.runOnMethods(PreRegistries.class, (m, a)->m.getStatic());
        StaticInitializer.initialize(extractor, Stage.FIRST, true);
        StaticInitializer.initialize(extractor, Stage.FIRST, false);
        registries.extractInStages();
//        registries.initMod();
        registries.register();
        extractor.runOnMethods(ReloadListener.class, (m, a)-> ReloadListenerRegistry.register(a.type(), (preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2) -> (CompletableFuture<Void>)m.get(null, true, preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2), ResourceLocation.fromNamespaceAndPath(registries.mod_id, Registries.defaultToMethod(a.value(), m))));
        CommandManager.register(this);
        StaticInitializer.initialize(extractor, Stage.LAST, true);
        StaticInitializer.initialize(extractor, Stage.LAST, false);
    }

    public void initClient() {
        EnvExecutor.runInEnv(EnvType.CLIENT, ()->()->{
            ClientRegistries client_registries = clientRegistries();
            StaticInitializer.initialize(extractor, ClientStage.FIRST, true);
            StaticInitializer.initialize(extractor, ClientStage.FIRST, false);
            client_registries.extractInStages();
            client_registries.register();
            ScrollHandler.register();
            StaticInitializer.initialize(extractor, ClientStage.LAST, true);
//            Platform.getMod(registries.mod_id).registerConfigurationScreen(parent->new ConfigScreen(parent, configs));
            StaticInitializer.initialize(extractor, ClientStage.LAST, false);
        });
    }

    @Environment(EnvType.CLIENT)
    public ClientRegistries clientRegistries() {
        return (ClientRegistries)client;
    }
}
