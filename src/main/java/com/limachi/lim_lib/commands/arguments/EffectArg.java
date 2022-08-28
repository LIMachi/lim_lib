package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MobEffectArgument;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EffectArg extends AbstractCommandArgument {
    public EffectArg() { type = MobEffectArgument.effect(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{MobEffect.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(MobEffectArgument.getEffect(ctx, getLabel())); }
}
