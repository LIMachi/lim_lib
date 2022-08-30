package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class PlayerArg extends AbstractCommandArgument {
    public PlayerArg() { type = EntityArgument.player(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ServerPlayer.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->EntityArgument.getPlayer(ctx, getLabel());
    }
}
