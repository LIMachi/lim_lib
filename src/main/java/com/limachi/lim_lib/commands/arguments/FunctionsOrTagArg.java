package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.FunctionArgument;

@SuppressWarnings("unused")
public class FunctionsOrTagArg extends AbstractCommandArgument {
    public FunctionsOrTagArg() { type = FunctionArgument.functions(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Pair.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->FunctionArgument.getFunctionOrTag(ctx, getLabel());
    }
}
