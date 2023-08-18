package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.CommandManager;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ItemPredicateArg extends AbstractCommandArgument {
    public ItemPredicateArg() { type = ItemPredicateArgument.itemPredicate(
            // VERSION 1.18.2
            CommandManager.builderContext // VERSION 1.19.2
    ); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Predicate.class, ItemStack.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ItemPredicateArgument.getItemPredicate(ctx, getLabel());
    }
}
