package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.world.scores.PlayerTeam;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class TeamArg extends AbstractCommandArgument {
    public TeamArg() { type = TeamArgument.team(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{PlayerTeam.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(TeamArgument.getTeam(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
