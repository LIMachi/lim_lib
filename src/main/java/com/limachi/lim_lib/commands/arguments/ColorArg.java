package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ColorArgument;

@SuppressWarnings("unused")
public class ColorArg extends AbstractCommandArgument {
    public ColorArg() { type = ColorArgument.color(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ChatFormatting.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ColorArgument.getColor(ctx, getLabel()); }
}
