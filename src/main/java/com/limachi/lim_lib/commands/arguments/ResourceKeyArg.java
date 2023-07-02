package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

/*
@SuppressWarnings("unused")
public class ResourceKeyArg<T> extends AbstractCommandArgument {
    public ResourceKeyArg(ResourceKey<? extends Registry<T>> registry) { type = ResourceKeyArgument.key(registry); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Attribute.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceKeyArgument.getAttribute(ctx, getLabel());
    }
}
*/