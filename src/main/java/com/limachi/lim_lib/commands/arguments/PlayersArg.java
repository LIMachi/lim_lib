package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class PlayersArg extends AbstractCommandArgument {
    private final boolean optional;
    public PlayersArg(boolean optional) {
        type = EntityArgument.players();
        this.optional = optional;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, ServerPlayer.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx -> {
            try {
                if (optional)
                    return Optional.of(EntityArgument.getOptionalPlayers(ctx, getLabel()));
                return Optional.of(EntityArgument.getPlayers(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
