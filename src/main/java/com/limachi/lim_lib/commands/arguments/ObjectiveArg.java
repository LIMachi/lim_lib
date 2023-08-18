package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.world.scores.Objective;

@SuppressWarnings("unused")
public class ObjectiveArg extends AbstractCommandArgument {
    private final boolean writable;
    public ObjectiveArg(boolean writable) {
        type = ObjectiveArgument.objective();
        this.writable = writable;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Objective.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        if (writable)
            return ctx->ObjectiveArgument.getWritableObjective(ctx, getLabel());
        return ctx->ObjectiveArgument.getObjective(ctx, getLabel());
    }
}
