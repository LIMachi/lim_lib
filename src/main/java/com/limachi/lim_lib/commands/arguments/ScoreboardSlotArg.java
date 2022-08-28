package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ScoreboardSlotArg extends AbstractCommandArgument {
    public ScoreboardSlotArg() { type = ScoreboardSlotArgument.displaySlot(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{int.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(ScoreboardSlotArgument.getDisplaySlot(ctx, getLabel())); }
}
