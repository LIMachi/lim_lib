package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;

import java.util.Collection;
import java.util.Optional;
import com.limachi.lim_lib.commands.FunctionThrowsCommandSyntaxException;

@SuppressWarnings("unused")
public class ProfileArg extends AbstractCommandArgument {
    public ProfileArg() { type = GameProfileArgument.gameProfile(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, GameProfile.class}; }
    @Override
    public FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object> getter() {
        return ctx->GameProfileArgument.getGameProfiles(ctx, getLabel());
    }
}
