package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TimeArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class TimeArg extends AbstractCommandArgument {
    public TimeArg() { type = TimeArgument.time(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{int.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->IntegerArgumentType.getInteger(ctx, getLabel()); }
}
