package com.limachi.lim_lib.test;

import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.registries.clientAnnotations.RegisterMenuScreen;
//import com.limachi.lim_lib.screens.WidgetContainerScreen;
import com.limachi.lim_lib.render.GuiUtils;
import com.limachi.lim_lib.widgets.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@RegisterMenuScreen(skip = "com.limachi.lim_lib.LimLib:useTests")
public class TestScreen extends AbstractContainerScreen<TestMenu> {

    private static final Minecraft mc = Minecraft.getInstance();

    Container t = new SimpleContainer(new ItemStack(Items.ANDESITE));

    boolean state = true;

    AnchoredBox s1 = AnchoredBox.topLeftDeltaBox(20, 20, 140, 40);
    AnchoredBox s2 = AnchoredBox.centeredBox(220, 60);

    public TestScreen(TestMenu menu, Inventory inventory, Component title) {

        super(menu, inventory, title);
//        addWidget(new BackgroundWidget(s1.copy()) {
//            @Override
//            public void tick() {
//                if (!widgetOptions.catchMouseEvents())
//                    widgetOptions.catchMouseEvents(true);
//            }
//
//            @Override
//            protected boolean onMouseClicked(double mouseX, double mouseY, int button) {
//                if (!isOvered || isAreaAnimated()) return false;
//                animateArea(state ? s2 : s1, 10);
//                state = !state;
//                return true;
//            }
//        }.addChild(new BackgroundWidget(AnchoredBox.topRightBox(120, 30)) {
//            @Nullable
//            @Override
//            protected List<Component> getTooltip() {
//                return isOvered ? Collections.singletonList(Component.literal("Hovered!")) : null;
//            }
//        }.addChild(new TextFieldWidget(AnchoredBox.centeredBox(100, 20)).setText("Test")))
//                        .addChild(new ButtonWidget(AnchoredBox.topLeftDeltaBox(0, 0, 20, 20), Component.literal("B"), b->{}))
//        );
//        addWidget(new TestSlotWidget(new AnchoredBox(AnchorPoint.TOP_RIGHT, 18, 18, AnchorPoint.TOP_LEFT), new Slot(t, 0, 0, 0)));
//        addWidget(new ScrollBarWidget(AnchoredBox.topRightBox(16, 100), 0, 10, 1, 3, w->{}));
//        addWidget(new TextWidget(AnchoredBox.centeredBox(100, 20), Component.literal("Test string")));
//        addWidget(new TestRenderWidget(100, 20));
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTick, int mouseX, int mouseY) {
        GuiUtils.background(gui);
        GuiUtils.slots(gui, 10, 10, 2, 2, true);
        GuiUtils.string(gui, new ScreenRectangle(new ScreenPosition(5, 20), 30, 10), Component.literal("a very long literal string to test wraps"), 0xFFFFFFFF);
        GuiUtils.entity(gui, mc.player, 10, 40);
    }
}