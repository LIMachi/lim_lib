package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class ResourceLocationArg extends AbstractCommandArgument {
    public ResourceLocationArg() { type = ResourceLocationArgument.id(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ResourceLocation.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceLocationArgument.getId(ctx, getLabel());
    }
}
