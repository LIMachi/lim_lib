package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

@SuppressWarnings("unused")
public class PredicateResourceLocationArg extends AbstractCommandArgument {
    public PredicateResourceLocationArg() { type = ResourceLocationArgument.id(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{LootItemCondition.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceLocationArgument.getPredicate(ctx, getLabel());
    }
}
