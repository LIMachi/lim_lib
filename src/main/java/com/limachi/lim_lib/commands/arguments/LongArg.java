package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class LongArg extends AbstractCommandArgument {
    public LongArg() { type = LongArgumentType.longArg(); }
    public LongArg(long min) {
        type = LongArgumentType.longArg(min);
        setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min, "" + min + 1, "" + min + 2}, builder));
    }
    public LongArg(long min, long max) {
        type = LongArgumentType.longArg(min, max);
        if (max - min >= 6)
            setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"" + min, "" + (min + 1), "" + (min + 2), "" + (max - 2), "" + (max - 1), "" + max}, builder));
        else {
            final String[] s = new String[(int)(max - min + 1)];
            for (long i = min; i <= max; ++i)
                s[(int)(i - min)] = "" + i;
            setSuggestions((ctx, builder) -> SharedSuggestionProvider.suggest(s, builder));
        }
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{long.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(LongArgumentType.getLong(ctx, getLabel())); }
}
