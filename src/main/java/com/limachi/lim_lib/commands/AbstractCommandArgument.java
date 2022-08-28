package com.limachi.lim_lib.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractCommandArgument {
    protected ArgumentType<?> type = null;
    private String label;
    private ArgumentBuilder<CommandSourceStack, ?> builder = null;
    private Predicate<CommandSourceStack> pred = null;
    private SuggestionProvider<CommandSourceStack> customSuggestions = null;
    public abstract Function<CommandContext<CommandSourceStack>, Optional<Object>> getter();
    public abstract Class<?>[] debugGetType();
    public String debugType() {
        Class<?>[] g = debugGetType();
        if (g.length == 1)
            return "" + g[0];
        else if (g.length == 2)
            return g[0] + "<" + g[1] + ">";
        return "!incorrect type!";
    }
    public ArgumentBuilder<CommandSourceStack, ?> builder() {
        if (builder == null) {
            if (type != null)
                builder = Commands.argument(label, type);
            else
                builder = Commands.literal(label);
            if (pred != null)
                builder.requires(pred);
            if (customSuggestions != null && builder instanceof RequiredArgumentBuilder rb)
                rb.suggests(customSuggestions);
        }
        return builder;
    }
    public AbstractCommandArgument setSuggestions(SuggestionProvider<CommandSourceStack> customSuggestions) { this.customSuggestions = customSuggestions; return this; }
    public AbstractCommandArgument setPredicate(Predicate<CommandSourceStack> pred) { this.pred = pred; return this; }
    public AbstractCommandArgument setLabel(String label) { this.label = label; return this; }
    public String getLabel() { return label; }
    public boolean isLiteral() { return type == null; }
}
