package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class StringArg extends AbstractCommandArgument {
    public StringArg() { type = StringArgumentType.greedyString(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{String.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(StringArgumentType.getString(ctx, getLabel())); }
}
