package com.limachi.lim_lib.screens;

import com.limachi.lim_lib.menus.AutoScaleMenu;
import com.limachi.lim_lib.registries.clientAnnotations.RegisterMenuScreen;
import com.limachi.lim_lib.render.RenderUtils;
import com.limachi.lim_lib.widgets.ScrollBarWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@RegisterMenuScreen
public class AutoScaleScreen extends AbstractContainerScreen<AutoScaleMenu> {

    protected final ScrollBarWidget scrollBar = new ScrollBarWidget(152, 15, 110, 0, 0, this::reorganiseSlots);

    public AutoScaleScreen(AutoScaleMenu menu, Inventory playerInventory, Component title) { super(menu, playerInventory, title); }

    @Override
    protected void init() {
        imageHeight = menu.getRows() * 18 + 112;
        inventoryLabelY = imageHeight - 94;
        if (menu.validSlots() > 54) {
            scrollBar.setMax(menu.pages(), true);
            addRenderableWidget(scrollBar);
            scrollBar.attachToScreen(this);
        }
        super.init();
    }

    protected void reorganiseSlots() {
        int page = scrollBar.getScroll();
        List<Slot> slots = menu.getSlots();
        int i = 36;
        for (; i < slots.size() && i < 36 + 8 * page; ++i) {
            Slot slot = slots.get(i);
            slot.x = Integer.MIN_VALUE;
            slot.y = Integer.MIN_VALUE;
        }
        for (int row = 0; row < 6 && i < slots.size(); ++row)
            for (int column = 0; column < 8 && i < slots.size(); ++column) {
                Slot slot = slots.get(i++);
                slot.x = 8 + column * 18;
                slot.y = 17 + row * 18;
            }
        for (; i < slots.size(); ++i) {
            Slot slot = slots.get(i);
            slot.x = Integer.MIN_VALUE;
            slot.y = Integer.MIN_VALUE;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double prevX, double prevY) {
        for (GuiEventListener widget : children())
            if (widget.mouseDragged(mouseX, mouseY, button, prevX, prevY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, prevX, prevY);
    }

    protected int validSlots() {
        int t = menu.validSlots();
        if (t <= 54) return t;
        return t - scrollBar.getScroll() * 8;
    }

    @Override
    protected void renderBg(PoseStack stack, float tick, int mouseX, int mouseY) { //FIXME
        renderBackground(stack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.background(this, stack);
        RenderUtils.playerSlots(this, stack, 7, 29 + menu.getRows() * 18, true);
        RenderUtils.slots(this, stack, 7 + (menu.validSlots() <= 9 ? 3 * 18 : 0), 16, menu.getRows(), menu.getColumns(), validSlots());
    }
}
