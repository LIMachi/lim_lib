package com.limachi.lim_lib.test;

import com.limachi.lim_lib.World;
import com.limachi.lim_lib.commands.CommandManager;
import com.limachi.lim_lib.commands.arguments.*;
import com.limachi.lim_lib.registries.StaticInit;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

@StaticInit
public class TestCommand {
    static {
        CommandManager.registerCmd(TestCommand.class, "enter_bag_cmd", "/test_commands enter <entities> <dimension>", new EntitiesArg(false), new DimensionArg());
        CommandManager.registerCmd(TestCommand.class, "enter_bag_self_cmd", "/test_commands enter <dimension>", new DimensionArg());
        CommandManager.registerCmd(null, "/test_commands", new SwizzleArg(), new BiomeArg(), new CompoundTagArg());
    }

    public static int enter_bag_cmd(CommandContext<CommandSourceStack> ctx, Collection<? extends Entity> entities, ServerLevel dim) {
        entities.forEach(e->World.teleportEntity(e, dim.dimension(), new Vec3(8, 128, 8)));
        ctx.getSource().sendSuccess(new TextComponent("Sent entities " + entities + " to the dim " + dim), true);
        return entities.size();
    }

    public static int enter_bag_self_cmd(CommandContext<CommandSourceStack> ctx, ServerLevel dim) {
        Entity e = ctx.getSource().getEntity();
        World.teleportEntity(e, dim.dimension(), new Vec3(8, 128, 8));
        ctx.getSource().sendSuccess(new TextComponent("Sent yourself " + e + " to the dim " + dim), true);
        return 1;
    }
}
