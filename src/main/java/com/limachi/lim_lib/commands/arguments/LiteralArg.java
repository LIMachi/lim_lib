package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

@SuppressWarnings("unused")
public class LiteralArg extends AbstractCommandArgument {
    private final boolean ignored;
    public LiteralArg(boolean ignored) { this.ignored = ignored; }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{String.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ignored ? null : ctx->getLabel(); }
}
