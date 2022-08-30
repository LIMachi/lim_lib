package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

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
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        if (optional)
            return ctx->EntityArgument.getOptionalPlayers(ctx, getLabel());
        return ctx->EntityArgument.getPlayers(ctx, getLabel());
    }
}
