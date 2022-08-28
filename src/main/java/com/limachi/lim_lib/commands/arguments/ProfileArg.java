package com.limachi.lim_lib.commands.arguments;

import com.limachi.lim_lib.commands.AbstractCommandArgument;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.GameProfileArgument;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("unused")
public class ProfileArg extends AbstractCommandArgument {
    public ProfileArg() { type = GameProfileArgument.gameProfile(); }
    @Override
    public Class<?>[] debugGetType() { return new Class[]{Collection.class, GameProfile.class}; }
    @Override
    public Function<CommandContext<CommandSourceStack>, Optional<Object>> getter() {
        return ctx-> {
            try {
                return Optional.of(GameProfileArgument.getGameProfiles(ctx, getLabel()));
            } catch (CommandSyntaxException e) {
                return Optional.empty();
            }
        };
    }
}
