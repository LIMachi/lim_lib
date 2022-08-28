package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ObjectiveCriteriaArg extends AbstractCommandArgument {
    public ObjectiveCriteriaArg() { type = ObjectiveCriteriaArgument.criteria(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ObjectiveCriteria.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(ObjectiveCriteriaArgument.getCriteria(ctx, getLabel())); }
}
