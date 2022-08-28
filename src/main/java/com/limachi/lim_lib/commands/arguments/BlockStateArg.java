package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class BlockStateArg extends AbstractCommandArgument {
    public BlockStateArg() { type = BlockStateArgument.block(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{BlockInput.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(BlockStateArgument.getBlock(ctx, getLabel())); }
}
