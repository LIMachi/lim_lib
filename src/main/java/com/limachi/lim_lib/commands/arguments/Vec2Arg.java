package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.world.phys.Vec2;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Vec2Arg extends AbstractCommandArgument {
    public Vec2Arg(boolean centerCorrect) { type = Vec2Argument.vec2(centerCorrect); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Vec2.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(Vec2Argument.getVec2(ctx, getLabel())); }
}
