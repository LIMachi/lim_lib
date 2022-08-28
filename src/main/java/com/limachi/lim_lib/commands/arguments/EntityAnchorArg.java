package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EntityAnchorArg extends AbstractCommandArgument {
    public EntityAnchorArg() { type = EntityAnchorArgument.anchor(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{EntityAnchorArgument.Anchor.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(EntityAnchorArgument.getAnchor(ctx, getLabel())); }
}
