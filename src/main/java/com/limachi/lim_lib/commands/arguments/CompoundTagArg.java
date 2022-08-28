package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class CompoundTagArg extends AbstractCommandArgument {
    public CompoundTagArg() { type = CompoundTagArgument.compoundTag(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{CompoundTag.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(CompoundTagArgument.getCompoundTag(ctx, getLabel())); }
}
