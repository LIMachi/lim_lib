package com.limachi.lim_lib;

import com.limachi.lim_lib.common.annotations.CmdArg;
import com.limachi.lim_lib.common.annotations.RegisterCommand;
import com.limachi.lim_lib.common.reflect.ReflectUtils;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;

import java.util.ArrayList;
import java.util.Collections;

public class LibCommands {
    // /lim_lib access net.minecraft.world.level.block.RedStoneWireBlock both true H W S E
    @RegisterCommand("lim_lib access <classPath> <read/write/both>")
    @RegisterCommand("lim_lib access <classPath> <read/write/both> <...field/methods>")
    @RegisterCommand("lim_lib access <classPath> <read/write/both> <addComments>")
    @RegisterCommand("lim_lib access <classPath> <read/write/both> <addComments> <...field/methods>")
    @RegisterCommand("lim_lib access <classPath> <transient> <read/write/both>")
    @RegisterCommand("lim_lib access <classPath> <transient> <read/write/both> <...field/methods>")
    @RegisterCommand("lim_lib access <classPath> <transient> <read/write/both> <addComments>")
    @RegisterCommand("lim_lib access <classPath> <transient> <read/write/both> <addComments> <...field/methods>")
    public static int getAccessWidenerCommand(CommandContext<CommandSourceStack> ctx,
                                              @CmdArg("classPath") String className,
                                              @CmdArg("transient") boolean trans,
                                              @CmdArg("read/write/both") String mode,
                                              @CmdArg("addComments") boolean addComments,
                                              @CmdArg(value = "...field/methods", matcher = StringArgumentType.StringType.GREEDY_PHRASE) String fieldsAndMethods
    ) {
        CommandSourceStack source = ctx.getSource();
        ReflectUtils.AccessWidening access = switch (mode) {
            case "read" -> trans ? ReflectUtils.AccessWidening.READ_TRANSITIVE : ReflectUtils.AccessWidening.READ;
            case "write" -> trans ? ReflectUtils.AccessWidening.WRITE_TRANSITIVE : ReflectUtils.AccessWidening.WRITE;
            case "both" -> trans ? ReflectUtils.AccessWidening.READ_WRITE_TRANSITIVE : ReflectUtils.AccessWidening.READ_WRITE;
            default -> null;
        };
        if (access == null) {
            source.sendFailure(Component.literal("invalid mode: " + mode));
            return 0;
        }
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            source.sendFailure(Component.literal("invalid class: " + className + " (don't forget to include the path of the class, ex: RedStoneWireBlock -> net.minecraft.world.level.block.RedStoneWireBlock)"));
            return 0;
        }
        String result = null;
        if (fieldsAndMethods != null && !fieldsAndMethods.isBlank()) {
            var s = fieldsAndMethods.split("\s");
            if (s.length > 0)
                result = ReflectUtils.declaredAccessWidener(clazz, access, addComments, s);
        }
        if (result == null)
            result = ReflectUtils.fullClassWidener(clazz, access);
        final String res = result;
        source.sendSuccess(()->Component.literal(res).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, res)).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.copy.click")))), false);
        return 1;
    }

    @RegisterCommand("lim_lib configs <mod_id>")
    public static int getConfigsCommand(CommandContext<CommandSourceStack> ctx, @CmdArg("mod_id") String modId) {
        CommandSourceStack source = ctx.getSource();
        boolean[] success = new boolean[]{true};
        String res = ModInstances.runOnMod(modId, m->m.configs.dump(), ()->{success[0] = false; return "Invalid mod id: " + modId;});
        source.sendSuccess(()->Component.literal(res), false);
        return success[0] ? 1 : 0;
    }

    @RegisterCommand("lim_lib mods")
    public static int getModsCommand(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        var list = new ArrayList<>(ModInstances.getAllModIds());
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        for (var modId : list)
            sb.append(modId).append("\n");
        source.sendSuccess(()->Component.literal(sb.toString()), false);
        return list.size();
    }
}
