package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.CommandManager;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;

@SuppressWarnings("unused")
public class ItemArg extends AbstractCommandArgument {
    public ItemArg() { type = ItemArgument.item(
            // VERSION 1.18.2
            CommandManager.builderContext // VERSION 1.19.2
    ); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{ItemInput.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ItemArgument.getItem(ctx, getLabel()); }
}
