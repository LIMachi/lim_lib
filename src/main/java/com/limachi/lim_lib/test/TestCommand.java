package com.limachi.lim_lib.test;

import com.limachi.lim_lib.World;
import com.limachi.lim_lib.commands.CommandManager;
import com.limachi.lim_lib.commands.arguments.*;
import com.limachi.lim_lib.registries.Stage;
import com.limachi.lim_lib.registries.StaticInit;
import com.limachi.lim_lib.render.BlockRenderUtils;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent; //VERSION 1.18.2
//import net.minecraft.network.chat.Component; //VERSION 1.19.2
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;

public class TestCommand {
    @StaticInit(skip = "com.limachi.lim_lib.LimLib:useTests")
    public static void test() {
//    static {
        CommandManager.registerCmd(TestCommand.class, "enter_dim", s->s.hasPermission(2), "/test_commands enter <entities?> <dimension?>", new EntitiesArg(true), new DimensionArg());
//        CommandManager.registerCmd(TestCommand.class, "enter_bag_self_cmd", "/test_commands enter <dimension>", new DimensionArg());
        CommandManager.registerCmd(TestCommand.class, "test_literal", "/test_commands test <int>", new IntArg(0, 100), new SwizzleArg(), new BiomeArg(), new CompoundTagArg());
        CommandManager.registerCmd(TestCommand.class, "test_literal", "/test_commands pred <literal>", new LiteralArg(false).requirePerm(2));
        CommandManager.registerCmd(TestCommand.class, "test_source", "/test_commands test_source");
        CommandManager.registerCmd(TestCommand.class, "test_empty", "/test_commands test_empty");
        CommandManager.registerCmd(TestCommand.class, "test_overlay", "/test_commands test_overlay <at>", new BlockPosArg());
    }

    public static int test_overlay(CommandSourceStack src, BlockPos pos) throws CommandSyntaxException {
        Player player = src.getPlayerOrException();
        BlockRenderUtils.queueOverlayRenderer(player.level, pos, null, BlockRenderUtils.DEFAULT_OVERLAY, 100);
        return 1;
    }

    public static int enter_dim(CommandContext<CommandSourceStack> ctx, Collection<? extends Entity> entities, ServerLevel dim) {
        if (entities == null)
            entities = Collections.singletonList(ctx.getSource().getEntity());
        ResourceKey<Level> level = dim != null ? dim.dimension() : Level.OVERWORLD;
        BlockPos pos = new BlockPos(8, 128, 8);
        for (Entity e : entities)
            World.teleportEntity(e, level, pos);
        ctx.getSource().sendSuccess(
                new TextComponent( //VERSION 1.18.2
//                Component.literal( //VERSION 1.19.2
                        "Sent entities " + entities + " to the dim " + dim), true);
        return entities.size();
    }

//    public static int enter_bag_self_cmd(CommandContext<CommandSourceStack> ctx, ServerLevel dim) {
//        Entity e = ctx.getSource().getEntity();
//        World.teleportEntity(e, dim.dimension(), new Vec3(8, 128, 8));
//        ctx.getSource().sendSuccess(new TextComponent("Sent yourself " + e + " to the dim " + dim), true);
//        return 1;
//    }

    public static int test_literal(CommandContext<CommandSourceStack> ctx, String literal) {
        ctx.getSource().sendSuccess(
                new TextComponent( //VERSION 1.18.2
//                Component.literal( //VERSION 1.19.2
                        "tested literal: " + literal), true);
        return 0;
    }

    public static int test_source(CommandSourceStack source) {
        source.sendSuccess(
                new TextComponent( //VERSION 1.18.2
//                Component.literal( //VERSION 1.19.2
                "good"), true);
        return 0;
    }

    public static int test_empty() { return 0; }
}
