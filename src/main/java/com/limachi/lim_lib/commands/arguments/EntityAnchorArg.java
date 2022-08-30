package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class EntityAnchorArg extends AbstractCommandArgument {
    public EntityAnchorArg() { type = EntityAnchorArgument.anchor(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{EntityAnchorArgument.Anchor.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() { return ctx->EntityAnchorArgument.getAnchor(ctx, getLabel()); }
}
