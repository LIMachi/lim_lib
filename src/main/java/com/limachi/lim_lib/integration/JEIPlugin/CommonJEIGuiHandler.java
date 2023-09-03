package com.limachi.lim_lib.integration.JEIPlugin;

import com.limachi.lim_lib.screens.IDontShowJEI;
import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;

import java.util.Collection;
import java.util.Collections;

public class CommonJEIGuiHandler implements IGlobalGuiHandler {
    @Override
    public Collection<Rect2i> getGuiExtraAreas() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof IDontShowJEI)
            return Collections.singletonList(new Rect2i(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE));
        return IGlobalGuiHandler.super.getGuiExtraAreas();
    }
}
