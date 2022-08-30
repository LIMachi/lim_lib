package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.TestClassNameArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class TestClassNameArg extends AbstractCommandArgument {
    public TestClassNameArg() { type = TestClassNameArgument.testClassName(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{String.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->TestClassNameArgument.getTestClassName(ctx, getLabel()); }
}
