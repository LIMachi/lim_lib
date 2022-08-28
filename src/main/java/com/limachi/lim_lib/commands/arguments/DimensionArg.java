package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DimensionArg extends AbstractCommandArgument {
    public DimensionArg() { type = DimensionArgument.dimension(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ServerLevel.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(DimensionArgument.getDimension(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
