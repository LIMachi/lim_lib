package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchoredBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/*
public class ToggleButtonWidget extends BaseButtonWidget<ToggleButtonWidget> {
    public ToggleButtonWidget(@NotNull AnchoredBox box, Component title, Consumer<ToggleButtonWidget> onStateChange) {
        super(box, title, onStateChange);
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (isOvered)
            updatePressedState(!pressedState, button, true);
        return isOvered;
    }

    public ToggleButtonWidget withPressedState(boolean state) {
        updatePressedState(state, 0, false);
        return this;
    }
}
*/