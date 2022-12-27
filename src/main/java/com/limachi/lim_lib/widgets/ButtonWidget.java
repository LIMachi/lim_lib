package com.limachi.lim_lib.widgets;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ButtonWidget extends BaseButtonWidget<ButtonWidget> {
    public ButtonWidget(int x, int y, int w, int h, Component title) { super(x, y, w, h, title); }
}
