package com.limachi.lim_lib.commands;

import com.limachi.lim_lib.Reflection;
import com.limachi.lim_lib.commands.arguments.LiteralArg;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public class CommandManager {
    private static final ArrayList<Pair<Integer, LiteralArgumentBuilder<CommandSourceStack>>> CMDS = new ArrayList<>();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CMDS.forEach(c->event.getDispatcher().register(c.getSecond()));
    }

    public static void registerCmd(Class<?> clazz, String method, String cmd, AbstractCommandArgument ... vargs) {
        registerCmd(clazz != null && method != null && !method.equals("") ? new Reflection.VargMethod(clazz, method) : null, null, cmd, vargs);
    }

    public static void registerCmd(Class<?> clazz, String method, Predicate<CommandSourceStack> requires, String cmd, AbstractCommandArgument ... vargs) {
        registerCmd(clazz != null && method != null && !method.equals("") ? new Reflection.VargMethod(clazz, method) : null, requires, cmd, vargs);
    }

    public static void registerCmd(Reflection.VargMethod execute, String cmd, AbstractCommandArgument ... vargs) {
        registerCmd(execute, null, cmd, vargs);
    }

    @SuppressWarnings("unchecked")
    public static void registerCmd(Reflection.VargMethod execute, Predicate<CommandSourceStack> requires, String cmd, AbstractCommandArgument ... vargs) {
        ArrayList<AbstractCommandArgument> args = new ArrayList<>();
        ArrayList<FunctionThrowsCommandSyntaxException<CommandContext<CommandSourceStack>, Object>> getters = new ArrayList<>();
        String[] c = cmd.startsWith("/") ? cmd.substring(1).split(" ") : cmd.split(" ");
        int i = 1;
        int p = 0;
        args.add(new LiteralArg().setLabel(c[0]).setPredicate(requires));
        while (i < c.length) {
            if (c[i].startsWith("<") && c[i].endsWith(">")) {
                String label = c[i].substring(1, c[i].length() - 1);
                if (label.isBlank()) {
                    //FIXME: ERROR
                    return;
                }
                args.add(vargs[p].setLabel(label));
                getters.add(vargs[p].getter());
                ++p;
            } else
                args.add(new LiteralArg().setLabel(c[i]));
            ++i;
        }
        Command<CommandSourceStack> test;
        if (execute != null)
            test = ctx-> {
                Object[] params = new Object[getters.size() + 1];
                params[0] = ctx;
                for (int j = 0; j < getters.size(); ++j)
                    params[j + 1] = getters.get(j).apply(ctx);
                return execute.invoke(params);
            };
        else
            test = ctx -> {
                StringBuilder output = new StringBuilder("Debugging command '" + cmd + "' -> public static method(CommandContext<CommandSourceStack> var1");
                if (vargs.length > 0) {
                    for (int j = 0; j < vargs.length; ++j)
                        output.append(", ").append(vargs[j].debugType()).append(" var").append(j + 2);
                }
                ctx.getSource().sendSuccess(new TextComponent(output.append(')').toString()), true);
                return getters.size();
            };
        i = args.size();
        args.get(i - 1).builder().executes(test);
        while (--i > 0)
            args.get(i - 1).builder().then(args.get(i).builder());
        int j = 0;
        while (j < CMDS.size() && CMDS.get(j).getFirst() <= args.size()) ++j;
        CMDS.add(j, new Pair<>(args.size(), (LiteralArgumentBuilder<CommandSourceStack>)args.get(0).builder()));
    }
}
