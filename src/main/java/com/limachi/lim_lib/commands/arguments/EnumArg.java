package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.server.command.EnumArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

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
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->ctx.getArgument(getLabel(), enumClass); }
}
