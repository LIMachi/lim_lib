package com.limachi.lim_lib.fabric;

import com.limachi.lim_lib.InstancedMod;
import com.limachi.lim_lib.ModInstances;
import com.limachi.lim_lib.common.annotations.Loader;
import com.limachi.lim_lib.common.modCreation.Loaders;

import com.limachi.lim_lib.client.annotations.FabricLayer;

import com.limachi.lim_lib.fabric.annotations.FabricMod;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.world.level.block.Block;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.function.Supplier;

@Loader(Loaders.Fabric)
public abstract class FabricEntryPoint implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
    private InstancedMod mod = null;

    public InstancedMod Mod() { return mod; }

    @Override
    public void onInitialize() {
        mod = ModInstances.newMod(getClass(), getClass().getAnnotation(FabricMod.class).value(), commonRootPackage(), CheckEnvironmentVisitor::skipInvalidEnv);
    }

    @Override
    public void onInitializeClient() {
        mod = ModInstances.getMod(super.getClass().getAnnotation(FabricMod.class).value());
        mod.initClient();
        mod.extractor.runAnnotations(FabricLayer.class, null, (f, a)->{
            FabricLayer.Layers layer = FabricLayer.Layers.fromString(a.value());
            if (layer == null)
                throw new InvalidParameterException("Invalid layer name for FabricLayer: " + a.value());
            Object t = f.get();
            if (t instanceof Supplier sup) {
                if (sup.get() instanceof Block b)
                    BlockRenderLayerMap.INSTANCE.putBlock(b, layer.renderType());
                else
                    throw new InvalidParameterException("FabricLayer supplier returned unexpected type: " + t);
            } else
                throw new InvalidParameterException("Invalid field type for FabricLayer: " + f.type());
        }, (m, a)->{
            FabricLayer.Layers layer = FabricLayer.Layers.fromString(a.value());
            if (layer == null)
                throw new InvalidParameterException("Invalid layer name for FabricLayer: " + a.value());
            Object t = m.get();
            if (t instanceof Collection col) {
                for (Object o : col)
                    if (o instanceof Block b)
                        BlockRenderLayerMap.INSTANCE.putBlock(b, layer.renderType());
                    else
                        throw new InvalidParameterException("FabricLayer method returned unexpected type in collection: " + o);
            } else
                throw new InvalidParameterException("Invalid method return type for FabricLayer: " + m.returnType());
        });
    }

    @Override
    public void onInitializeServer() {
        mod = ModInstances.getMod(super.getClass().getAnnotation(FabricMod.class).value());
//        mod.initServer();
    }

    protected abstract String commonRootPackage();

    @Loader(Loaders.Fabric)
    public static class CheckEnvironmentVisitor extends ClassVisitor {
        final String onlyIn = Environment.class.descriptorString();
        final String dist = EnvType.class.descriptorString();
        final String mixin = Mixin.class.descriptorString();
        final String loader = Loader.class.descriptorString();
        boolean skip = false;

        protected CheckEnvironmentVisitor() { super(Opcodes.ASM9); }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals(onlyIn))
                return new OnlyInVisitor();
            else if (descriptor.equals(mixin))
                skip = true;
            else if (descriptor.equals(loader))
                return new LoaderVisitor();
            return super.visitAnnotation(descriptor, visible);
        }

        @Loader(Loaders.Fabric)
        protected class OnlyInVisitor extends AnnotationVisitor {
            protected OnlyInVisitor() { super(Opcodes.ASM9); }

            @Override
            public void visitEnum(String name, String descriptor, String value) {
                if (descriptor.equals(dist) && name.equals("value") && FabricLoader.getInstance().getEnvironmentType() != EnvType.valueOf(value))
                    skip = true;
                super.visitEnum(name, descriptor, value);
            }
        }

        @Loader(Loaders.Fabric)
        protected class LoaderVisitor extends AnnotationVisitor {
            protected LoaderVisitor() { super(Opcodes.ASM9); }

            @Override
            public void visitEnum(String name, String descriptor, String value) {
                if (descriptor.equals(loader) && name.equals("value") && !Loaders.Fabric.matches(Loaders.valueOf(value)))
                    skip = true;
                super.visitEnum(name, descriptor, value);
            }
        }

        public static boolean skipInvalidEnv(ClassReader cr) {
            var check = new CheckEnvironmentVisitor();
            cr.accept(check, ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE);
            return check.skip;
        }
    }
}
