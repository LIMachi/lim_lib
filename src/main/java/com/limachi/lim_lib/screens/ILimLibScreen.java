package com.limachi.lim_lib.screens;

import net.minecraft.network.chat.Component;

import java.util.List;

/** marker interface used by screen to signal widgets that they will be handled as expected */
public interface ILimLibScreen {
    boolean overrideTooltip(List<Component> tooltip);
}
