package com.limachi.lim_lib.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface FunctionThrowsCommandSyntaxException<T, R> {
    R apply(T t) throws CommandSyntaxException;
}
