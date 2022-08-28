package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class EnumArg<T extends Enum<T>> extends AbstractCommandArgument {
    private final Class<T> enumClass;
    public EnumArg(Class<T> enumClass) {
        type = EnumArgument.enumArgument(enumClass);
        this.enumClass = enumClass;
    }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{enumClass}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() { return ctx->Optional.of(ctx.getArgument(getLabel(), enumClass)); }
}
