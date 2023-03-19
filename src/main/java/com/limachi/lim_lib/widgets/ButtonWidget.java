package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchoredBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ButtonWidget extends BaseButtonWidget<ButtonWidget> {

    public ButtonWidget(@NotNull AnchoredBox box, Component title, Consumer<ButtonWidget> onStateChange) {
        super(box, title, onStateChange);
    }

    @Override
    protected void onMouseStopOver(double mouseX, double mouseY) {
        updatePressedState(false, 0);
        super.onMouseStopOver(mouseX, mouseY);
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        updatePressedState(isOvered, button);
        return isOvered;
    }

    @Override
    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        boolean t = pressedState;
        updatePressedState(false, button);
        return t;
    }
}
