package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.world.scores.PlayerTeam;

@SuppressWarnings("unused")
public class TeamArg extends AbstractCommandArgument {
    public TeamArg() { type = TeamArgument.team(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{PlayerTeam.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->TeamArgument.getTeam(ctx, getLabel());
    }
}
