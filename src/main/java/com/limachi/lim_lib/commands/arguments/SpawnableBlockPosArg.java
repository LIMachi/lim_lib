package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SpawnableBlockPosArg extends AbstractCommandArgument {
    public SpawnableBlockPosArg() { type = BlockPosArgument.blockPos(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{BlockPos.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(BlockPosArgument.getSpawnablePos(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
