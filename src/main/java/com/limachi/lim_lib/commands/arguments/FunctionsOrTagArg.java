package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.FunctionArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class FunctionsOrTagArg extends AbstractCommandArgument {
    public FunctionsOrTagArg() { type = FunctionArgument.functions(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Pair.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(FunctionArgument.getFunctionOrTag(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
