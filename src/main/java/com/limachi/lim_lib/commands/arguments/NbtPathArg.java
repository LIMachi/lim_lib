package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;

@SuppressWarnings("unused")
public class NbtPathArg extends AbstractCommandArgument {
    public NbtPathArg() { type = NbtPathArgument.nbtPath(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{NbtPathArgument.NbtPath.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->NbtPathArgument.getPath(ctx, getLabel()); }
}
