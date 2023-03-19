package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType; //VERSION 1.19.2
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biome;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class BiomeArg extends AbstractCommandArgument {
//    private static final DynamicCommandExceptionType ERROR_BIOME_INVALID = new DynamicCommandExceptionType(t->Component.translatable("commands.locate.biome.invalid", t)); //VERSION 1.19.2
    public BiomeArg() { type = ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceOrTagLocationArgument.Result.class, Biome.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceOrTagLocationArgument.getBiome(ctx, getLabel()); //VERSION 1.18.2
//        return ctx->ResourceOrTagLocationArgument.getRegistryType(ctx, getLabel(), Registry.BIOME_REGISTRY, ERROR_BIOME_INVALID); //VERSION 1.19.2
    }
}
