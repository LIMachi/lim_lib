package com.limachi.lim_lib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

public class TestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("enter_bag").requires(pred -> pred.hasPermission(2))
                .then(Commands.argument("entities", EntityArgument.entities()).then(Commands.argument("id", IntegerArgumentType.integer(0, 0))
                .executes(cmd -> enter_bag(cmd.getSource(), EntityArgument.getEntities(cmd, "entities"), IntegerArgumentType.getInteger(cmd, "id"))))));
    }

    private static int enter_bag(CommandSourceStack source, Collection<? extends Entity> entities, int id) {
//        for (Entity e : entities)
//            World.teleportEntity(e, Constants.BAG_DIM, new BlockPos(8, 128, 8));
        source.sendSuccess(new TextComponent("Sent entities " + entities + " to the bag id " + id), true);
        return 0;
    }
}
