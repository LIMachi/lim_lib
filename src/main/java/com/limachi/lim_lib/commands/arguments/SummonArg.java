package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class SummonArg extends AbstractCommandArgument {
    public SummonArg() { type = EntitySummonArgument.id(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceLocation.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(EntitySummonArgument.getSummonableEntity(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
