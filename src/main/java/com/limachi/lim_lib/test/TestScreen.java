package com.limachi.lim_lib.test;

import com.limachi.lim_lib.registries.clientAnnotations.RegisterMenuScreen;
import com.limachi.lim_lib.screens.AbstractWidgetContainerScreen;
import com.limachi.lim_lib.widgets.ButtonWidget;
import com.limachi.lim_lib.widgets.CyclingButtonWidget;
import com.limachi.lim_lib.widgets.TextFieldWidget;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

@RegisterMenuScreen(skip = "com.limachi.lim_lib.LimLib:useTests")
public class TestScreen extends AbstractWidgetContainerScreen<TestMenu> {

    public TestScreen(TestMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        initWidgets.add(new TextFieldWidget(20, 20, 140).setOnFinish(t->t.clearTooltip().addTooltip(Component.literal(t.getText()))).setValidator((s, t)->!s.contains("fuck")));
        initWidgets.add(new ButtonWidget(20, 40, 80, 16, Component.literal("Test Button")).setOnStateChange(b->{
            if (b.getPressedState())
                b.addTooltip(Component.literal("Got Pressed!"));
            else
                b.clearTooltip();
        }));
        initWidgets.add(new CyclingButtonWidget(20, 60, 80, 16, Component.literal("Test 1"), Component.literal("Test 2"), Component.literal("Test 3")).setOnStateChange(c->c.clearTooltip().addTooltip(c.getValues().get(c.getSelected()))));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
