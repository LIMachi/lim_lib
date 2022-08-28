package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.FunctionArgument;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class FunctionsArg extends AbstractCommandArgument {
    public FunctionsArg() { type = FunctionArgument.functions(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, CommandFunction.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(FunctionArgument.getFunctions(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
