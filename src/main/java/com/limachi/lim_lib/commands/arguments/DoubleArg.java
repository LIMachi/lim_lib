package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;

@SuppressWarnings("unused")
public class DoubleArg extends AbstractCommandArgument {
    public DoubleArg() { type = DoubleArgumentType.doubleArg(); }
    public DoubleArg(double min) {
        type = DoubleArgumentType.doubleArg(min);
        setSuggestions("" + min, "" + min * 10., "" + min * 100., "" + min * 1000.);
    }
    public DoubleArg(double min, double max) {
        type = DoubleArgumentType.doubleArg(min, max);
        if (min != max)
            setSuggestions("" + min, "" + ((max - min) / 2. + min), "" + max);
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{float.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->DoubleArgumentType.getDouble(ctx, getLabel()); }
}
