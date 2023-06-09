package com.limachi.lim_lib.capabilities;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProviderImpl;
import net.minecraftforge.common.util.LazyOptional;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"UnstableApiUsage", "unused"})
public class Cap {

    public static <Target extends ICapabilityProviderImpl<?>, Ret, Data, Token extends CapabilityToken<Data>> Ret run(Target target, Token cap, Function<Data, Ret> run, Ret def) {
        LazyOptional<Data> opt = target.getCapability(CapabilityManager.get(cap));
        try {
            return run.apply(opt.orElseThrow(RuntimeException::new));
        } catch (Exception e) {
            return def;
        }
    }

    public static <Target extends ICapabilityProviderImpl<?>, Ret, Data, Cap extends Capability<Data>> Ret run(Target target, Cap cap, Function<Data, Ret> run, Ret def) {
        LazyOptional<Data> opt = target.getCapability(cap);
        try {
            return run.apply(opt.orElseThrow(RuntimeException::new));
        } catch (Exception e) {
            return def;
        }
    }

    public static <Target extends ICapabilityProviderImpl<?>, Ret, Data, Token extends CapabilityToken<Data>> Ret run(Target target, Token cap, Direction direction, Function<Data, Ret> run, Ret def) {
        LazyOptional<Data> opt = target.getCapability(CapabilityManager.get(cap), direction);
        try {
            return run.apply(opt.orElseThrow(RuntimeException::new));
        } catch (Exception e) {
            return def;
        }
    }

    public static <Target extends ICapabilityProviderImpl<?>, Ret, Data, Cap extends Capability<Data>> Ret run(Target target, Cap cap, Direction direction, Function<Data, Ret> run, Ret def) {
        LazyOptional<Data> opt = target.getCapability(cap, direction);
        try {
            return run.apply(opt.orElseThrow(RuntimeException::new));
        } catch (Exception e) {
            return def;
        }
    }

    public static <Target extends ICapabilityProviderImpl<?>, Data, Token extends CapabilityToken<Data>> boolean run(Target target, Token cap, Consumer<Data> run) {
        LazyOptional<Data> opt = target.getCapability(CapabilityManager.get(cap));
        try {
            run.accept(opt.orElseThrow(RuntimeException::new));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <Target extends ICapabilityProviderImpl<?>, Data, Cap extends Capability<Data>> boolean run(Target target, Cap cap, Consumer<Data> run) {
        LazyOptional<Data> opt = target.getCapability(cap);
        try {
            run.accept(opt.orElseThrow(RuntimeException::new));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <Target extends ICapabilityProviderImpl<?>, Data, Token extends CapabilityToken<Data>> boolean run(Target target, Token cap, Direction direction, Consumer<Data> run) {
        LazyOptional<Data> opt = target.getCapability(CapabilityManager.get(cap), direction);
        try {
            run.accept(opt.orElseThrow(RuntimeException::new));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <Target extends ICapabilityProviderImpl<?>, Data, Cap extends Capability<Data>> boolean run(Target target, Cap cap, Direction direction, Consumer<Data> run) {
        LazyOptional<Data> opt = target.getCapability(cap, direction);
        try {
            run.accept(opt.orElseThrow(RuntimeException::new));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
