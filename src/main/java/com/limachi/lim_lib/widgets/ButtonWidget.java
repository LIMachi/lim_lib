package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchoredBox;
import net.minecraft.network.chat.Component;


import javax.annotation.Nonnull;
import java.util.function.Consumer;

/*
public class ButtonWidget extends BaseButtonWidget<ButtonWidget> {

    public ButtonWidget(@Nonnull AnchoredBox box, Component title, Consumer<ButtonWidget> onStateChange) {
        super(box, title, onStateChange);
    }

    @Override
    protected void onMouseStopOver(double mouseX, double mouseY) {
        if (pressedState)
            updatePressedState(false, 0, true);
        super.onMouseStopOver(mouseX, mouseY);
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        updatePressedState(isOvered, button, true);
        return isOvered;
    }

    @Override
    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        boolean t = pressedState;
        if (pressedState)
            updatePressedState(false, button, true);
        return t;
    }
}
*/