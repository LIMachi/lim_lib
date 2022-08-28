package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ItemPredicateArg extends AbstractCommandArgument {
    public ItemPredicateArg() { type = ItemPredicateArgument.itemPredicate(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Predicate.class, ItemStack.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(ItemPredicateArgument.getItemPredicate(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
