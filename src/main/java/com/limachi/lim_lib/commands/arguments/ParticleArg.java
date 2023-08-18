package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.CommandManager;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;

@SuppressWarnings("unused")
public class ParticleArg extends AbstractCommandArgument {
    public ParticleArg() { type = ParticleArgument.particle(CommandManager.builderContext); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ParticleOptions.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ParticleArgument.getParticle(ctx, getLabel()); }
}
