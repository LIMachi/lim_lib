package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ScoreHolderArgument;

@SuppressWarnings("unused")
public class ScoreHolderArg extends AbstractCommandArgument {
    public ScoreHolderArg() { type = ScoreHolderArgument.scoreHolder(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{String.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ScoreHolderArgument.getName(ctx, getLabel());
    }
}
