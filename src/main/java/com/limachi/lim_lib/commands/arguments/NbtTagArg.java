package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.Tag;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class NbtTagArg extends AbstractCommandArgument {
    public NbtTagArg() { type = NbtTagArgument.nbtTag(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Tag.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->NbtTagArgument.getNbtTag(ctx, getLabel()); }
}
