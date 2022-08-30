package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class ParticleArg extends AbstractCommandArgument {
    public ParticleArg() { type = ParticleArgument.particle(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ParticleOptions.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ParticleArgument.getParticle(ctx, getLabel()); }
}
