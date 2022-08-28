package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class FeatureKeyArg extends AbstractCommandArgument {
    public FeatureKeyArg() { type = ResourceKeyArgument.key(Registry.CONFIGURED_FEATURE_REGISTRY); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Holder.class, ConfiguredFeature.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(ResourceKeyArgument.getConfiguredFeature(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
