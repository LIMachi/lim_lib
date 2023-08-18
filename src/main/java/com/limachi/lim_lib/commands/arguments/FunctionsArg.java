package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.FunctionArgument;

import java.util.Collection;

@SuppressWarnings("unused")
public class FunctionsArg extends AbstractCommandArgument {
    public FunctionsArg() { type = FunctionArgument.functions(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, CommandFunction.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->FunctionArgument.getFunctions(ctx, getLabel());
    }
}
