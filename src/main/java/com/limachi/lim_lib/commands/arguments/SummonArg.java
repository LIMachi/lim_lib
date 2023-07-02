package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.resources.ResourceLocation;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

/*
@SuppressWarnings("unused")
public class SummonArg extends AbstractCommandArgument {
    public SummonArg() { type = EntitySummonArgument.id(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceLocation.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->EntitySummonArgument.getSummonableEntity(ctx, getLabel());
    }
}
*/