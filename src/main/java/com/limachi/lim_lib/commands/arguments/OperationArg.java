package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.OperationArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class OperationArg extends AbstractCommandArgument {
    public OperationArg() { type = OperationArgument.operation(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{OperationArgument.Operation.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->OperationArgument.getOperation(ctx, getLabel()); }
}
