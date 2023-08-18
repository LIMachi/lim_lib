package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

@SuppressWarnings("unused")
public class BlockPosArg extends AbstractCommandArgument {
    public BlockPosArg() { type = BlockPosArgument.blockPos(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{BlockPos.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->BlockPosArgument.getLoadedBlockPos(ctx, getLabel());
    }
}
