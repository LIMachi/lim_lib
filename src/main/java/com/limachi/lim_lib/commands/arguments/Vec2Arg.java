package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Vec2Argument;
import net.minecraft.world.phys.Vec2;

@SuppressWarnings("unused")
public class Vec2Arg extends AbstractCommandArgument {
    public Vec2Arg(boolean centerCorrect) { type = Vec2Argument.vec2(centerCorrect); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Vec2.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->Vec2Argument.getVec2(ctx, getLabel()); }
}
