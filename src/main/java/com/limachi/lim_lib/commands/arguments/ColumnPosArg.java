package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.server.level.ColumnPos;

@SuppressWarnings("unused")
public class ColumnPosArg extends AbstractCommandArgument {
    public ColumnPosArg() { type = ColumnPosArgument.columnPos(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ColumnPos.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ColumnPosArgument.getColumnPos(ctx, getLabel()); }
}
