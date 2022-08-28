package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.AngleArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class AngleArg extends AbstractCommandArgument {
    public AngleArg() { type = AngleArgument.angle(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{float.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(AngleArgument.getAngle(ctx, getLabel())); }
}
