package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.CommandManager;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;

@SuppressWarnings("unused")
public class BlockStateArg extends AbstractCommandArgument {
    public BlockStateArg() { type = BlockStateArgument.block(
            //VERSION 1.18.2
            CommandManager.builderContext // VERSION 1.19.2
    ); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{BlockInput.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->BlockStateArgument.getBlock(ctx, getLabel()); }
}
