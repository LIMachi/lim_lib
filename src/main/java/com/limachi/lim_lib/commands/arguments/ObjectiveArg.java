package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.world.scores.Objective;

import java.util.Optional;
import java.util.function.Function;

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
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                if (writable)
                    return Optional.of(ObjectiveArgument.getWritableObjective(ctx, getLabel()));
                return Optional.of(ObjectiveArgument.getObjective(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
