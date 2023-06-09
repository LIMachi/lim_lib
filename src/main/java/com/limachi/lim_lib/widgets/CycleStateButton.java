package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchoredBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class CycleStateButton extends BaseButtonWidget<CycleStateButton> {
    protected final BaseWidget<?>[] states;
    protected int index;

    public CycleStateButton(@NotNull AnchoredBox box, Consumer<CycleStateButton> onStateChange, int starting_index, BaseWidget<?> ... states) {
        super(box, Component.empty(), onStateChange);
        index = Integer.min(starting_index, states.length - 1);
        this.states = states;
        for (int i = 0; i < states.length; ++i) {
            states[i].widgetOptions.active(i == index && widgetOptions.active());
            if (i == index)
                addChild(states[index]);
        }
    }

    public int getIndex() { return index; }
    public BaseWidget<?> getState() { return states.length > 0 ? states[index] : null; }

    public void cycle(int amount) {
        if (states.length > 0) {
            int ni = index + amount;
            while (ni < 0)
                ni += states.length;
            while (ni >= states.length)
                ni -= states.length;
            if (ni != index) {
                states[index].node.removeNode();
                states[index].widgetOptions.active(false);
                states[ni].widgetOptions.active(widgetOptions.active());
                addChild(states[ni]);
                index = ni;
                if (onStateChange != null)
                    onStateChange.accept(this);
            }
        }
    }

    @Override
    protected boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        if (isOvered)
            cycle((int)amount);
        return isOvered;
    }

    @Override
    protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
        if (isOvered) {
            cycle(button == 0 ? 1 : button == 1 ? -1 : 0);
            updatePressedState(true, button, false);
        }
        return isOvered;
    }

    @Override
    protected void onMouseStopOver(double mouseX, double mouseY) {
        if (pressedState)
            updatePressedState(false, 0, false);
        super.onMouseStopOver(mouseX, mouseY);
    }

    @Override
    protected boolean onMouseReleased(double mouseX, double mouseY, int button) {
        boolean t = pressedState;
        if (pressedState)
            updatePressedState(false, button, false);
        return t;
    }
}
