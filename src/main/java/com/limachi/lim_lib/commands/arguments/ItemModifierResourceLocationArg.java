package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ItemModifierResourceLocationArg extends AbstractCommandArgument {
    public ItemModifierResourceLocationArg() { type = ResourceLocationArgument.id(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{LootItemFunction.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(ResourceLocationArgument.getItemModifier(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
