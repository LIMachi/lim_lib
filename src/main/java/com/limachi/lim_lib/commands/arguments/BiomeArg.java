package com.limachi.lim_lib.commands.arguments;

/*
@SuppressWarnings("unused")
public class BiomeArg extends AbstractCommandArgument {
    private static final DynamicCommandExceptionType ERROR_BIOME_INVALID = new DynamicCommandExceptionType(t->Component.translatable("commands.locate.biome.invalid", t)); //VERSION 1.19.2
    public BiomeArg() { type = ResourceOrTagLocationArgument.resourceOrTag(Registry.BIOME_REGISTRY); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceOrTagLocationArgument.Result.class, Biome.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
//        return ctx->ResourceOrTagLocationArgument.getBiome(ctx, getLabel()); //VERSION 1.18.2
        return ctx->ResourceOrTagLocationArgument.getRegistryType(ctx, getLabel(), Registry.BIOME_REGISTRY, ERROR_BIOME_INVALID); //VERSION 1.19.2
    }
}
*/