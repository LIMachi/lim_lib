package com.limachi.lim_lib.containers;

import net.minecraft.world.ContainerListener;

import java.util.Set;

public interface IContainerListenerHandler {
    default void addListener(ContainerListener listener) { listeners().add(listener); }
    default void removeListener(ContainerListener listener) { listeners().remove(listener); }
    Set<ContainerListener> listeners();
}
