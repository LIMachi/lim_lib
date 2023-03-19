package com.limachi.lim_lib.widgetsOld;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
@OnlyIn(Dist.CLIENT)
public class CyclingButtonWidget extends BaseButtonWidget<CyclingButtonWidget> {

    public static final Component MISSING_TITLE = Component.empty();
    protected final ArrayList<Component> values = new ArrayList<>();
    protected int selected = 0;

    public CyclingButtonWidget(int x, int y, int w, int h, List<Component> choices) {
        super(x, y, w, h, choices.size() > 0 ? choices.get(0) : MISSING_TITLE);
        values.addAll(choices);
    }

    public CyclingButtonWidget(int x, int y, int w, int h, Component ... choices) {
        this(x, y, w, h, Arrays.stream(choices).toList());
    }

    @Override
    protected void updatePressedState(boolean state, int button) {
        if (state && !pressedState) {
            if (values.size() > 0) {
                selected += button == 0 ? 1 : button == 1 ? -1 : 0;
                if (selected < 0)
                    selected = values.size() - 1;
                if (selected >= values.size())
                    selected = 0;
                title = values.get(selected);
            } else
                title = MISSING_TITLE;
        }
        super.updatePressedState(state, button);
    }

    public int getSelected() { return selected; }

    public ArrayList<Component> getValues() { return values; }
}
