package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

@SuppressWarnings("unused")
public class WordArg extends AbstractCommandArgument {
    public WordArg() { type = StringArgumentType.word(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{String.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->StringArgumentType.getString(ctx, getLabel()); }
}
