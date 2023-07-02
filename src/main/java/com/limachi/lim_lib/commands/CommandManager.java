package com.limachi.lim_lib.commands;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.commands.arguments.LiteralArg;
import com.limachi.lim_lib.reflection.MethodHolder;
import com.limachi.lim_lib.reflection.Types;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.network.chat.TextComponent; //VERSION 1.18.2
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component; //VERSION 1.19.2
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "rawtypes"})
@Mod.EventBusSubscriber
public class CommandManager {
    private static final ArrayList<Pair<Integer, LiteralArgumentBuilder<CommandSourceStack>>> CMDS = new ArrayList<>();

    public static String builderAsStrings(LiteralArgumentBuilder<CommandSourceStack> builder) {
        StringBuilder out = new StringBuilder("/");
        out.append(builder.getLiteral());
        Collection<CommandNode<CommandSourceStack>> nodes = builder.getArguments();
        while (!nodes.isEmpty()) {
            CommandNode<CommandSourceStack> node = nodes.iterator().next();
            if (node instanceof LiteralCommandNode l)
                out.append(' ').append(l.getLiteral());
            if (node instanceof ArgumentCommandNode a)
                out.append(' ').append(a.getUsageText());
            nodes = node.getChildren();
        }
        return out.toString();
    }

//    public static CommandBuildContext builderContext = new CommandBuildContext(RegistryAccess.BUILTIN.get()); //VERSION 1.19.2
    public static CommandBuildContext builderContext = CommandBuildContext.configurable(RegistryAccess.EMPTY, FeatureFlagSet.of()); //VERSION 1.20.1

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CMDS.forEach(c->{
            builderContext = event.getBuildContext(); //technically do nothing as 'builderContext' probably already was referenced in the construction of commands
            Log.LOGGER.info("Registering command: " + builderAsStrings(c.getSecond()));
            event.getDispatcher().register(c.getSecond());
        });
    }

    public static void registerCmd(Class<?> clazz, String method, String cmd, AbstractCommandArgument ... vargs) {
        registerCmd(clazz, method, null, cmd, vargs);
    }

    public static void registerCmd(Class<?> clazz, String method, Predicate<CommandSourceStack> requires, String cmd, AbstractCommandArgument ... vargs) {
        MethodHolder tm = clazz != null && method != null && !method.isBlank() ? MethodHolder.fromFirstMatching(clazz, null, method) : null;
        if (tm != null)
            registerCmd(tm, requires, cmd, vargs);
    }

    public static void registerCmd(MethodHolder execute, String cmd, AbstractCommandArgument ... vargs) {
        registerCmd(execute, null, cmd, vargs);
    }

    @SuppressWarnings("unchecked")
    public static void registerCmd(MethodHolder execute, Predicate<CommandSourceStack> requires, String cmd, AbstractCommandArgument ... vargs) {
        ArrayList<AbstractCommandArgument> args = new ArrayList<>();
        ArrayList<AbstractCommandArgument> nonLiteral = new ArrayList<>();
        String[] c = cmd.startsWith("/") ? cmd.substring(1).split(" ") : cmd.split(" ");
        int i = 1;
        int p = 0;
        boolean isAlreadyBranching = false;
        args.add(new LiteralArg(true).setLabel(c[0]).setPredicate(requires));
        while (i < c.length) {
            if (c[i].startsWith("<") && c[i].endsWith(">")) {
                String label = c[i].substring(1, c[i].length() - 1);
                if (label.isBlank()) {
                    Log.error(cmd, "Invalid blank label <> in command declaration.");
                    return;
                }
                if (label.contains("?")) {
                    if (!isAlreadyBranching) {
                        StringBuilder tcmd = new StringBuilder();
                        tcmd.append("/").append(c[0]);
                        for (int j = 1; j < c.length; ++j)
                            if (j != i)
                                tcmd.append(" ").append(c[j]);
                            else
                                tcmd.append(" ").append(c[j].replace("?", "!"));
                        registerCmd(execute, tcmd.toString(), vargs);
                    }
                    label = label.replace("?", "");
                }
                if (label.contains("!"))
                    isAlreadyBranching = true;
                else
                    args.add(vargs[p].setLabel(label));
                nonLiteral.add(vargs[p]);
                ++p;
            } else
                args.add(new LiteralArg(true).setLabel(c[i]));
            ++i;
        }
        while (p < vargs.length) {
            nonLiteral.add(vargs[p]);
            ++p;
        }
        Command<CommandSourceStack> test;
        if (execute != null)
            test = ctx-> {
                int pt = 0;
                Object[] params;
                Class<?>[] opt = execute.getOptionalParameterTypes();
                if (opt != null && (CommandContext.class.isAssignableFrom(opt[0]) || CommandSourceStack.class.isAssignableFrom(opt[0]))) {
                    params = new Object[nonLiteral.size() + 1];
                    params[0] = CommandContext.class.isAssignableFrom(opt[0]) ? ctx : ctx.getSource();
                    pt = 1;
                }
                else
                    params = new Object[nonLiteral.size()];
                for (int j = 0; j < nonLiteral.size(); ++j, ++pt) {
                    AbstractCommandArgument argument = nonLiteral.get(j);
                    try {
                        params[pt] = argument.getter().apply(ctx);
                    } catch (CommandSyntaxException forward) {
                        throw forward;
                    } catch (IllegalArgumentException forward) {
                        try {
                            params[pt] = argument.getDefault() == null ? Types.getDefault(argument.debugGetType()[0]) : argument.getDefault().apply(ctx);
                        } catch (Exception ignored) {
                            throw forward;
                        }
                    } catch (Exception caught) {
                        ctx.getSource().sendFailure(
//                                new TextComponent( //VERSION 1.18.2
                                Component.literal( //VERSION 1.19.2
                                        "Exception in command '" + cmd + "' -> public static " + execute.getMethod(false) + ". Please check game log for further details."));
                        caught.printStackTrace();
                        return 0;
                    }
                }
                return execute.invoke(params);
            };
        else
            test = ctx -> {
                StringBuilder output = new StringBuilder("Debugging command '" + cmd + "' -> public static method(CommandContext<CommandSourceStack> var1");
                if (vargs.length > 0) {
                    for (int j = 0; j < vargs.length; ++j)
                        output.append(", ").append(vargs[j].debugType()).append(" var").append(j + 2);
                }
                ctx.getSource().sendSuccess(
//                        new TextComponent( //VERSION 1.18.2
                        ()->//VERSION 1.20.1
                        Component.literal( //VERSION 1.19.2
                                output.append(')').toString()), true);
                return nonLiteral.size();
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
