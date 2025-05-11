package com.limachi.lim_lib.neoforge;

import com.limachi.lim_lib.InstancedMod;
import com.limachi.lim_lib.ModInstances;
import com.limachi.lim_lib.common.annotations.Loader;
import com.limachi.lim_lib.common.modCreation.Loaders;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.OnlyIns;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.fml.common.Mod;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Loader(Loaders.NeoForge)
public abstract class NeoEntryPoint {
    public final InstancedMod mod;

    public NeoEntryPoint(IEventBus modBus, Dist dist) {
        mod = ModInstances.newMod(getClass(), getClass().getAnnotation(Mod.class).value(), commonRootPackage(), CheckDistVisitor::skipInvalidEnv);
        if (dist.isClient())
            modBus.addListener((Consumer<RenderLevelStageEvent.RegisterStageEvent>)e->{
                mod.initClient();
            });
        //FIXME: missing server only init
    }

    protected abstract String commonRootPackage();

    @Loader(Loaders.NeoForge)
    public static class CheckDistVisitor extends ClassVisitor {
        final String onlyIn = OnlyIn.class.descriptorString();
        final String onlyIns = OnlyIns.class.descriptorString();
        final String dist = Dist.class.descriptorString();
        final String mixin = Mixin.class.descriptorString();
        final String loader = Loader.class.descriptorString();
        boolean skip = false;
        protected CheckDistVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals(onlyIn))
                return new OnlyInVisitor();
            else if (descriptor.equals(onlyIns))
                return new OnlyInsVisitor();
            else if (descriptor.equals(mixin))
                skip = true;
            else if (descriptor.equals(loader))
                return new LoaderVisitor();
            return super.visitAnnotation(descriptor, visible);
        }

        @Loader(Loaders.NeoForge)
        protected class OnlyInVisitor extends AnnotationVisitor {
            protected OnlyInVisitor() { super(Opcodes.ASM9); }

            @Override
            public void visitEnum(String name, String descriptor, String value) {
                if (descriptor.equals(dist) && name.equals("value") && FMLEnvironment.dist != Dist.valueOf(value))
                    skip = true;
                super.visitEnum(name, descriptor, value);
            }
        }

        @Loader(Loaders.NeoForge)
        protected class LoaderVisitor extends AnnotationVisitor {
            protected LoaderVisitor() { super(Opcodes.ASM9); }

            @Override
            public void visitEnum(String name, String descriptor, String value) {
                if (descriptor.equals(loader) && name.equals("value") && !Loaders.NeoForge.matches(Loaders.valueOf(value)))
                    skip = true;
                super.visitEnum(name, descriptor, value);
            }
        }

        @Loader(Loaders.NeoForge)
        protected class OnlyInsVisitor extends AnnotationVisitor {
            protected OnlyInsVisitor() { super(Opcodes.ASM9); }

            @Override
            public AnnotationVisitor visitArray(String name) {
                if (name.equals("value"))
                    return new CheckDistVisitor.OnlyInArrayVisitor();
                return super.visitArray(name);
            }
        }

        @Loader(Loaders.NeoForge)
        protected class OnlyInArrayVisitor extends AnnotationVisitor {
            protected OnlyInArrayVisitor() { super(Opcodes.ASM9); }

            @Override
            public AnnotationVisitor visitAnnotation(String name, String descriptor) {
                if (descriptor.equals(onlyIn))
                    return new OnlyInVisitor();
                return super.visitAnnotation(name, descriptor);
            }
        }

        public static boolean skipInvalidEnv(ClassReader cr) {
            var check = new CheckDistVisitor();
            cr.accept(check, ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE);
            return check.skip;
        }
    }
}
