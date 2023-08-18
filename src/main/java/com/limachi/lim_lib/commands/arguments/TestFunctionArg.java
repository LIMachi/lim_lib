package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.gametest.framework.TestFunctionArgument;

@SuppressWarnings("unused")
public class TestFunctionArg extends AbstractCommandArgument {
    public TestFunctionArg() { type = TestFunctionArgument.testFunctionArgument(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{TestFunction.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->TestFunctionArgument.getTestFunction(ctx, getLabel()); }
}
