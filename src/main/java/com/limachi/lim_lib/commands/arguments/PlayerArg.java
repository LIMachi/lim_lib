package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class PlayerArg extends AbstractCommandArgument {
    public PlayerArg() { type = EntityArgument.player(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ServerPlayer.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx -> {
            try {
                return Optional.of(EntityArgument.getPlayer(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
