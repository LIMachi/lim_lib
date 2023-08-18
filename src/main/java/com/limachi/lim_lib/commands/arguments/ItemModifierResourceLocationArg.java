package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

@SuppressWarnings("unused")
public class ItemModifierResourceLocationArg extends AbstractCommandArgument {
    public ItemModifierResourceLocationArg() { type = ResourceLocationArgument.id(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{LootItemFunction.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceLocationArgument.getItemModifier(ctx, getLabel());
    }
}
