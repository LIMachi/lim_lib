package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.UuidArgument;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("unused")
public class UUIDArg extends AbstractCommandArgument {
    public UUIDArg() { type = UuidArgument.uuid(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{UUID.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(UuidArgument.getUuid(ctx, getLabel())); }
}
