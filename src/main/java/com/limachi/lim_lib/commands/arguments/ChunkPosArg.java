package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.world.level.ChunkPos;

@SuppressWarnings("unused")
public class ChunkPosArg extends AbstractCommandArgument {
    public ChunkPosArg() { type = ColumnPosArgument.columnPos(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ChunkPos.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ColumnPosArgument.getColumnPos(ctx, getLabel()).toChunkPos(); }
}
