package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

@SuppressWarnings("unused")
public class EntitiesArg extends AbstractCommandArgument {
    private final boolean optional;
    public EntitiesArg(boolean optional) {
        type = EntityArgument.entities();
        this.optional = optional;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, Entity.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        if (optional)
            return ctx->EntityArgument.getOptionalEntities(ctx, getLabel());
        return ctx->EntityArgument.getEntities(ctx, getLabel());
    }
}
