package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EntityArg extends AbstractCommandArgument {
    public EntityArg() { type = EntityArgument.entity(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Entity.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx -> {
            try {
                return Optional.of(EntityArgument.getEntity(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
