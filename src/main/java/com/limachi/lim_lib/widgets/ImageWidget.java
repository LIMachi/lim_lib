package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.maths.Box2d;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ImageWidget extends BaseWidget<ImageWidget> {

    public ImageWidget(@NotNull AnchoredBox box, ResourceLocation texture) {
        this(box, texture, new Box2d(box.area().x(), box.area().y()));
    }

    public ImageWidget(@NotNull AnchoredBox box, ResourceLocation texture, Box2d cutout) {
        super(box, new WidgetOptions());
        setBackground(texture, cutout);
    }
}
