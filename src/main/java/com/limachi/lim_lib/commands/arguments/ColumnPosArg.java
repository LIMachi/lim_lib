package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.server.level.ColumnPos;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ColumnPosArg extends AbstractCommandArgument {
    public ColumnPosArg() { type = ColumnPosArgument.columnPos(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ColumnPos.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(ColumnPosArgument.getColumnPos(ctx, getLabel())); }
}
