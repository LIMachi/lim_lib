package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ColorArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ColorArg extends AbstractCommandArgument {
    public ColorArg() { type = ColorArgument.color(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ChatFormatting.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(ColorArgument.getColor(ctx, getLabel())); }
}
