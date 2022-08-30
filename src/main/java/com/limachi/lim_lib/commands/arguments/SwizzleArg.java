package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.core.Direction;

import java.util.EnumSet;
import java.util.Optional;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class SwizzleArg extends AbstractCommandArgument {
    public SwizzleArg() { type = SwizzleArgument.swizzle(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{EnumSet.class, Direction.Axis.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->SwizzleArgument.getSwizzle(ctx, getLabel()); }
}
