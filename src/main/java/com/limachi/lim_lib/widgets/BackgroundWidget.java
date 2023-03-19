package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.LimLib;
import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.Box2d;
import net.minecraft.resources.ResourceLocation;

public class BackgroundWidget extends BaseWidget<BackgroundWidget> {
    public static final ResourceLocation BACKGROUND = new ResourceLocation(LimLib.COMMON_ID, "textures/screen/background.png");
    public static final Box2d CUTOUT = new Box2d(256, 256);

    public BackgroundWidget(AnchoredBox box) {
        super(box, new WidgetOptions().canAnimate(true));
        setBackground(BACKGROUND, CUTOUT);
    }
}
