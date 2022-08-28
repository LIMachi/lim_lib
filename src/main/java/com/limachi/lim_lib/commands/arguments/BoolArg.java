package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class BoolArg extends AbstractCommandArgument {
    public BoolArg() { type = BoolArgumentType.bool(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{boolean.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(BoolArgumentType.getBool(ctx, getLabel())); }
}
