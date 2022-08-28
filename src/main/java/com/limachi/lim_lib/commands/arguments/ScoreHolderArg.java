package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ScoreHolderArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ScoreHolderArg extends AbstractCommandArgument {
    public ScoreHolderArg() { type = ScoreHolderArgument.scoreHolder(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{String.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(ScoreHolderArgument.getName(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
