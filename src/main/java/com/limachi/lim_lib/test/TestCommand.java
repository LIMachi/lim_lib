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
        CommandManager.registerCmd(TestCommand.class, "enter_bag_cmd", s->s.hasPermission(2), "/test_commands enter <entities> <dimension>", new EntitiesArg(false), new DimensionArg()); //FIXME: here the predicate is overridden, or more exactly cannot be added to the command because other branches of this command have different requirements
        CommandManager.registerCmd(TestCommand.class, "enter_bag_self_cmd", "/test_commands enter <dimension>", new DimensionArg());
        CommandManager.registerCmd(null, "/test_commands test <int>", new IntArg(0, 100), new SwizzleArg(), new BiomeArg(), new CompoundTagArg());
        CommandManager.registerCmd(TestCommand.class, "test_literal", "/test_commands pred <literal>", new LiteralArg().requirePerm(2));
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

    public static int test_literal(CommandContext<CommandSourceStack> ctx, String literal) {
        ctx.getSource().sendSuccess(new TextComponent("tested literal: " + literal), true);
        return 0;
    }
}
