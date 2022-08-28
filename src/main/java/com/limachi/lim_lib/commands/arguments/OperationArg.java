package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.OperationArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class OperationArg extends AbstractCommandArgument {
    public OperationArg() { type = OperationArgument.operation(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{OperationArgument.Operation.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(OperationArgument.getOperation(ctx, getLabel())); }
}
