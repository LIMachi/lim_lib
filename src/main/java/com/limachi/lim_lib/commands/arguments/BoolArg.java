package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class BoolArg extends AbstractCommandArgument {
    public BoolArg() { type = BoolArgumentType.bool(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{boolean.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->BoolArgumentType.getBool(ctx, getLabel()); }
}
