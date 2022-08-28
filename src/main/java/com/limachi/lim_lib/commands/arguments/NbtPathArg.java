package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class NbtPathArg extends AbstractCommandArgument {
    public NbtPathArg() { type = NbtPathArgument.nbtPath(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{NbtPathArgument.NbtPath.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(NbtPathArgument.getPath(ctx, getLabel())); }
}
