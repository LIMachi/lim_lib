package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.CommandManager;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public class BlockPredicateArg extends AbstractCommandArgument {
    public BlockPredicateArg() { type = BlockPredicateArgument.blockPredicate(
            //VERSION 1.18.2
            CommandManager.builderContext //VERSION 1.19.2
    ); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Predicate.class, BlockInWorld.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->BlockPredicateArgument.getBlockPredicate(ctx, getLabel());
    }
}
