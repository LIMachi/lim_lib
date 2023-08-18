package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
public class MessageArg extends AbstractCommandArgument {
    public MessageArg() { type = MessageArgument.message(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Component.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->MessageArgument.getMessage(ctx, getLabel());
    }
}
