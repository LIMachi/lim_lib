package com.limachi.lim_lib.screens;

import com.limachi.lim_lib.render.RenderUtils;
//import com.limachi.lim_lib.widgetsOld.BaseWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
//import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*
public class AbstractWidgetContainerScreen<M extends AbstractContainerMenu> extends AbstractContainerScreen<M> implements ILimLibScreen {

    protected List<BaseWidget> initWidgets = new ArrayList<>();
    final ArrayList<Component> tooltipOverride = new ArrayList<>();

    public AbstractWidgetContainerScreen(M menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        for (BaseWidget w : initWidgets)
            addRenderableWidget(w);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return children().stream().noneMatch(w->w instanceof BaseWidget && !((BaseWidget)w).shouldCloseOnEsc());
    }

    @Override
    @Nonnull
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(@Nonnull T widget) {
        if (widget instanceof BaseWidget w)
            w.attachToScreen(this);
        return super.addRenderableWidget(widget);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        tooltipOverride.clear();
        super.render(poseStack, mouseX, mouseY, partialTick);
        if (tooltipOverride.size() > 0)
            renderTooltip(poseStack, tooltipOverride, Optional.empty(), mouseX, mouseY, ItemStack.EMPTY);
    }

    @Override
    public void setFocused(@Nullable GuiEventListener l) {
        super.setFocused(l);
        if (l instanceof BaseWidget w) {
            removeWidget(w);
            addRenderableWidget(w);
            if (initWidgets.removeIf(t->t == w))
                initWidgets.add(w);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double originalX, double originalY) {
        for (GuiEventListener widget : children())
            if (widget.mouseDragged(mouseX, mouseY, button, originalX, originalY)) return true;
        return super.mouseDragged(mouseX, mouseY, button, originalX, originalY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (GuiEventListener widget : children())
            widget.mouseMoved(mouseX, mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.background(this, poseStack);
    }

    @Override
    public boolean overrideTooltip(List<Component> tooltip) {
        if (tooltipOverride.size() > 0) return false;
        tooltipOverride.addAll(tooltip);
        return true;
    }
}
*/