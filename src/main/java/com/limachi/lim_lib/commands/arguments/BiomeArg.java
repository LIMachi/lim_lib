package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class BiomeArg extends AbstractCommandArgument {
    public BiomeArg() { type = ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceOrTagLocationArgument.Result.class, Biome.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(ResourceOrTagLocationArgument.getBiome(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
