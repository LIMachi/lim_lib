package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class BlockPredicateArg extends AbstractCommandArgument {
    public BlockPredicateArg() { type = BlockPredicateArgument.blockPredicate(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Predicate.class, BlockInWorld.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(BlockPredicateArgument.getBlockPredicate(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
