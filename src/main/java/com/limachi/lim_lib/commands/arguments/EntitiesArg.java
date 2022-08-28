package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

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
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx -> {
            try {
                if (optional)
                    return Optional.of(EntityArgument.getOptionalEntities(ctx, getLabel()));
                return Optional.of(EntityArgument.getEntities(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
