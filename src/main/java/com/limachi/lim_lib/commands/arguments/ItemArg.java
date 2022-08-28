package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ItemArg extends AbstractCommandArgument {
    public ItemArg() { type = ItemArgument.item(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ItemInput.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(ItemArgument.getItem(ctx, getLabel())); }
}
