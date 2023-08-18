package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.world.item.crafting.Recipe;

@SuppressWarnings("unused")
public class RecipeResourceLocationArg extends AbstractCommandArgument {
    public RecipeResourceLocationArg() { type = ResourceLocationArgument.id(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Recipe.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->ResourceLocationArgument.getRecipe(ctx, getLabel());
    }
}
