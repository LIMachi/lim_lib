package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Vec3Arg extends AbstractCommandArgument {
    public Vec3Arg(boolean centerCorrect) { type = Vec3Argument.vec3(centerCorrect); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Vec3.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(Vec3Argument.getVec3(ctx, getLabel())); }
}
