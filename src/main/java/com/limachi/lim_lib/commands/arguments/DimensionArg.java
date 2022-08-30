package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class DimensionArg extends AbstractCommandArgument {
    public DimensionArg() { type = DimensionArgument.dimension(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ServerLevel.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->DimensionArgument.getDimension(ctx, getLabel());
    }
}
