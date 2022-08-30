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
        setSuggestions("" + min, "" + (min * 10), "" + (min * 100), "" + (min * 1000));
    }
    public IntArg(int min, int max) {
        type = IntegerArgumentType.integer(min, max);
        setSuggestions((c,b)->{
            String in = b.getRemaining();
            if (in.isBlank())
                return SharedSuggestionProvider.suggest(min == max ? new String[]{"" + min} : min + 1 == max ? new String[]{"" + min, "" + max} : new String[]{"" + min, "" + (min + 1), "" + ((max - min) / 2L + min), "" + (max - 1), "" + max}, b);
            int t;
            try {
                t = Integer.parseInt(in) * 10;
            } catch (NumberFormatException e) {
                return SharedSuggestionProvider.suggest(new String[]{}, b);
            }
            if (t > max) return SharedSuggestionProvider.suggest(new String[]{}, b);
            String[] s;
            if (max - t >= 10) {
                s = new String[(t * 10 < max) ? 12 : 11];
                for (int i = 0; i < 10; ++i)
                    s[i] = "" + (i + t);
                s[10] = "" + max;
                if (t * 10 < max)
                    s[11] = "" + t * 10;
            } else {
                s = new String[max - min + 1];
                for (int i = min; i <= max; ++i)
                    s[i - min] = "" + i;
            }
            return SharedSuggestionProvider.suggest(s, b);
        });
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{int.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(IntegerArgumentType.getInteger(ctx, getLabel())); }
}
