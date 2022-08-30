package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class ObjectiveCriteriaArg extends AbstractCommandArgument {
    public ObjectiveCriteriaArg() { type = ObjectiveCriteriaArgument.criteria(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ObjectiveCriteria.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ObjectiveCriteriaArgument.getCriteria(ctx, getLabel()); }
}
