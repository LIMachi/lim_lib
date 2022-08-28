package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class IntArg extends AbstractCommandArgument {
    public IntArg() { type = IntegerArgumentType.integer(); }
    public IntArg(int min) {
        type = IntegerArgumentType.integer(min);
        setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min, "" + min + 1, "" + min + 2}, builder));
    }
    public IntArg(int min, int max) {
        type = IntegerArgumentType.integer(min, max);
        if (max - min >= 6)
            setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min, "" + (min + 1), "" + (min + 2), "" + (max - 2), "" + (max - 1), "" + max}, builder));
        else {
            final String[] s = new String[max - min + 1];
            for (int i = min; i <= max; ++i)
                s[i - min] = "" + i;
            setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(s, builder));
        }
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{int.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(IntegerArgumentType.getInteger(ctx, getLabel())); }
}
