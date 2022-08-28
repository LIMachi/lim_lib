package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class MessageArg extends AbstractCommandArgument {
    public MessageArg() { type = MessageArgument.message(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Component.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(MessageArgument.getMessage(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
