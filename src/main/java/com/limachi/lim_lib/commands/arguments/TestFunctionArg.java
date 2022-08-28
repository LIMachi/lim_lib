package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.gametest.framework.TestFunctionArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class TestFunctionArg extends AbstractCommandArgument {
    public TestFunctionArg() { type = TestFunctionArgument.testFunctionArgument(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{TestFunction.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(TestFunctionArgument.getTestFunction(ctx, getLabel())); }
}
