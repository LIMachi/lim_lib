package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;

@SuppressWarnings("unused")
public class CoordinatesArg extends AbstractCommandArgument {
    public CoordinatesArg(boolean centerCorrect) { type = Vec3Argument.vec3(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Coordinates.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->Vec3Argument.getCoordinates(ctx, getLabel()); }
}
