package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EnchantmentArg extends AbstractCommandArgument {
    public EnchantmentArg() { type = ItemEnchantmentArgument.enchantment(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Enchantment.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(ItemEnchantmentArgument.getEnchantment(ctx, getLabel())); }
}
