package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.SlotArgument;

@SuppressWarnings("unused")
public class SlotArg extends AbstractCommandArgument {
    public SlotArg() { type = SlotArgument.slot(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{int.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->SlotArgument.getSlot(ctx, getLabel()); }
}
