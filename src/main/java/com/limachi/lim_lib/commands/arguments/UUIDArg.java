package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.UuidArgument;
import java.util.UUID;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class UUIDArg extends AbstractCommandArgument {
    public UUIDArg() { type = UuidArgument.uuid(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{UUID.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->UuidArgument.getUuid(ctx, getLabel()); }
}
