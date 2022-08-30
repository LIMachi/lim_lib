package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class BiomeArg extends AbstractCommandArgument {
    public BiomeArg() { type = ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceOrTagLocationArgument.Result.class, Biome.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceOrTagLocationArgument.getBiome(ctx, getLabel());
    }
}
