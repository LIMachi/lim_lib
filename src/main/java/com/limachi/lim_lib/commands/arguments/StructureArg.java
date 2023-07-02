package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.arguments.ResourceOrTagLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
//import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

/*
 * WARN does not exist in 1.19.2, or at least not in this form
 */

//@SuppressWarnings("unused")
//public class StructureArg<T> extends AbstractCommandArgument {
//    public StructureArg(ResourceKey<? extends Registry<T>> registry) { type = ResourceOrTagLocationArgument.resourceOrTag(registry); }
//    @Override
//    public Class<?>[] debugGetType() { return new Class[]{ResourceOrTagLocationArgument.Result.class, net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature.class}; }
//    @Override
//    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
//        return ctx->ResourceOrTagLocationArgument.getStructureFeature(ctx, getLabel());
//    }
//}
