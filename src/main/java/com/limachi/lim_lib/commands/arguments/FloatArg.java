package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class FloatArg extends AbstractCommandArgument {
    public FloatArg() { type = FloatArgumentType.floatArg(); }
    public FloatArg(float min) {
        type = FloatArgumentType.floatArg(min);
        setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min}, builder));
    }
    public FloatArg(float min, float max) {
        type = FloatArgumentType.floatArg(min, max);
        setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min, "" + max}, builder));
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{float.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(FloatArgumentType.getFloat(ctx, getLabel())); }
}
