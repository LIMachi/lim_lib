package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class DoubleArg extends AbstractCommandArgument {
    public DoubleArg() { type = DoubleArgumentType.doubleArg(); }
    public DoubleArg(double min) {
        type = DoubleArgumentType.doubleArg(min);
        setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min}, builder));
    }
    public DoubleArg(double min, double max) {
        type = DoubleArgumentType.doubleArg(min, max);
        setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min, "" + max}, builder));
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{double.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(DoubleArgumentType.getDouble(ctx, getLabel())); }
}
