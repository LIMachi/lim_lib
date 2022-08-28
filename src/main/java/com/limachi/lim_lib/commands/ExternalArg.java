package com.limachi.lim_lib.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ExternalArg extends AbstractCommandArgument {
    protected final Function<CommandContext<CommandSourceStack>, Optional<Object>> getter;
    protected final Class<?> getterReturnType;
    public ExternalArg(ArgumentType<?> type, Function<CommandContext<CommandSourceStack>, Optional<Object>> getter, Class<?> getterReturnType) {
        this.type = type;
        this.getter = getter;
        this.getterReturnType = getterReturnType;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{getterReturnType}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return getter; }
}
