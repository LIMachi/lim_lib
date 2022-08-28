package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ScoreHolderArgument;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ScoreHoldersArg extends AbstractCommandArgument {
    private final boolean withWildcard;
    public ScoreHoldersArg(boolean withWildcard) {
        type = ScoreHolderArgument.scoreHolders();
        this.withWildcard = withWildcard;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, String.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                if (withWildcard)
                    return Optional.of(ScoreHolderArgument.getNamesWithDefaultWildcard(ctx, getLabel()));
                return Optional.of(ScoreHolderArgument.getNames(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
