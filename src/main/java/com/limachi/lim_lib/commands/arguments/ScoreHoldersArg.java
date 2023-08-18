package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ScoreHolderArgument;

import java.util.Collection;

@SuppressWarnings("unused")
public class ScoreHoldersArg extends AbstractCommandArgument {
    private final boolean withWildcard;
    public ScoreHoldersArg(boolean withWildcard) {
        type = ScoreHolderArgument.scoreHolders();
        this.withWildcard = withWildcard;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, String.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        if (withWildcard)
            return ctx->ScoreHolderArgument.getNamesWithDefaultWildcard(ctx, getLabel());
        return ctx->ScoreHolderArgument.getNames(ctx, getLabel());
    }
}
