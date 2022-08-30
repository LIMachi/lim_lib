package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class FeatureKeyArg extends AbstractCommandArgument {
    public FeatureKeyArg() { type = ResourceKeyArgument.key(Registry.CONFIGURED_FEATURE_REGISTRY); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Holder.class, ConfiguredFeature.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceKeyArgument.getConfiguredFeature(ctx, getLabel());
    }
}
