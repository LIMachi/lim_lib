package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.server.command.ModIdArgument;

@SuppressWarnings("unused")
public class ModIdArg extends AbstractCommandArgument {
    public ModIdArg() { type = ModIdArgument.modIdArgument(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{String.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ctx.getArgument(getLabel(), String.class); }
}
