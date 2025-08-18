package com.limachi.lim_lib.common.annotations;

import com.limachi.lim_lib.common.commands.CommandManager;

import java.lang.annotation.*;

@Repeatable(CommandManager.RegisterCommands.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RegisterCommand {
    /**
     * command pattern, ex:<br>
     * {@code @RegisterCommand("root cmd <int>")}<br>
     * {@code public static int myCommand(CommandContext<CommandSourceStack> ctx, @CmdArg("int") Integer test){ return test; }}<br>
     * <br>
     * would result in the command: `/root cmd 1` returning 1, `/root cmd 2` returning 2 and so on
     */
    String value();

    /**
     * 0: all
     * 1: moderator (access to spawn protection)
     * 2: gamemaster (access to datapack, advancements, forceload, xp, worldborder, difficulty, etc...)
     * 3: administrator (access to multiplayer management, kick, (de)op, whitelist, etc...)
     * 4: owner (access to server management, perf, save, stop, publish, etc...)
     */
    int OPLevel() default 0;

    /**
     * should this command only be available to players (and not command blocks, server, etc...)
     */
    boolean requirePlayer() default false;
}
