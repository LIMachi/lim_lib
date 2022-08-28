package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class StructureArg<T> extends AbstractCommandArgument {
    public StructureArg(ResourceKey<? extends Registry<T>> registry) { type = ResourceOrTagLocationArgument.resourceOrTag(registry); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceOrTagLocationArgument.Result.class, ConfiguredStructureFeature.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(ResourceOrTagLocationArgument.getStructureFeature(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
