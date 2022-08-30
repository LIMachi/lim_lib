package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.AngleArgument;

@SuppressWarnings("unused")
public class AngleArg extends AbstractCommandArgument {
    public AngleArg() { type = AngleArgument.angle(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{float.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->AngleArgument.getAngle(ctx, getLabel()); }
}
