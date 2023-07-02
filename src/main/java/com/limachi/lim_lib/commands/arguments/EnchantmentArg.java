package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.world.item.enchantment.Enchantment;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

/*
@SuppressWarnings("unused")
public class EnchantmentArg extends AbstractCommandArgument {
    public EnchantmentArg() { type = ItemEnchantmentArgument.enchantment(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Enchantment.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ItemEnchantmentArgument.getEnchantment(ctx, getLabel()); }
}
*/