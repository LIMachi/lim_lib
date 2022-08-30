package com.limachi.lim_lib.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

@SuppressWarnings("unused")
public class ExternalArg extends AbstractCommandArgument {
    protected final FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter;
    protected final Class<?> getterReturnType;
    public ExternalArg(ArgumentType<?> type, FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter, Class<?> getterReturnType) {
        this.type = type;
        this.getter = getter;
        this.getterReturnType = getterReturnType;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{getterReturnType}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return getter; }
}
