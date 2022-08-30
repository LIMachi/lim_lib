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
        setSuggestions("" + min, "" + (min * 10L), "" + (min * 100L), "" + (min * 1000L));
    }
    public LongArg(long min, long max) {
        type = LongArgumentType.longArg(min, max);
        setSuggestions((c,b)->{
            String in = b.getRemaining();
            if (in.isBlank())
                return SharedSuggestionProvider.suggest(min == max ? new String[]{"" + min} : min + 1L == max ? new String[]{"" + min, "" + max} : new String[]{"" + min, "" + (min + 1L), "" + ((max - min) / 2L + min), "" + (max - 1L), "" + max}, b);
            long t;
            try {
                t = Long.parseLong(in) * 10L;
            } catch (NumberFormatException e) {
                return SharedSuggestionProvider.suggest(new String[]{}, b);
            }
            if (t > max) return SharedSuggestionProvider.suggest(new String[]{}, b);
            String[] s;
            if (max - t >= 10) {
                s = new String[(t * 10L < max) ? 12 : 11];
                for (long i = 0; i < 10; ++i)
                    s[(int)i] = "" + (i + t);
                s[10] = "" + max;
                if (t * 10L < max)
                    s[11] = "" + t * 10L;
            } else {
                s = new String[(int)(max - min + 1)];
                for (long i = min; i <= max; ++i)
                    s[(int)(i - min)] = "" + i;
            }
            return SharedSuggestionProvider.suggest(s, b);
        });
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{long.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(LongArgumentType.getLong(ctx, getLabel())); }
}
