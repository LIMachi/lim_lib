package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ResourceKeyArg<T> extends AbstractCommandArgument {
    public ResourceKeyArg(ResourceKey<? extends Registry<T>> registry) { type = ResourceKeyArgument.key(registry); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Attribute.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(ResourceKeyArgument.getAttribute(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
