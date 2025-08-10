package com.limachi.lim_lib.tests;

import com.limachi.lim_lib.ModInstances;
import com.limachi.lim_lib.common.annotations.CmdArg;
import com.limachi.lim_lib.common.annotations.RegisterData;
import com.limachi.lim_lib.common.annotations.RegisterCommand;
import com.limachi.lim_lib.common.dataStorage.DataField;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TestData {
    @RegisterData(syncToClient = true, dimensions = {"minecraft:overworld", "minecraft:the_end"})
    public static final DataField<Integer> test = new DataField<>(0) {
        @Override
        public void receiveSync(Tag tag, ResourceLocation level) {
            super.receiveSync(tag, level);
            ModInstances.getModByOwnedClass(TestData.class).logger.info("client data sync received: " + get(level) + " for level " + level);
        }
    };

    @RegisterData(saveToDisk = false)
    public static final DataField<Integer> vol = new DataField<>(0);

    @RegisterCommand("lim_lib data_test set <int>")
    public static int dataSet(CommandContext<CommandSourceStack> ctx, @CmdArg("int") Integer i) {
        test.set(ctx.getSource().getLevel(), i);
        return 1;
    }

    @RegisterCommand("lim_lib data_test get")
    public static int dataGet(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(()-> Component.literal(test.get(ctx.getSource().getLevel()).toString()), true);
        return 1;
    }

    @RegisterCommand("lim_lib data_test_volatile set <int>")
    public static int dataSetVol(CommandContext<CommandSourceStack> ctx, @CmdArg("int") Integer i) {
        vol.set(ctx.getSource().getLevel(), i);
        return 1;
    }

    @RegisterCommand("lim_lib data_test_volatile get")
    public static int dataGetVol(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(()-> Component.literal(vol.get(ctx.getSource().getLevel()).toString()), true);
        return 1;
    }
}
