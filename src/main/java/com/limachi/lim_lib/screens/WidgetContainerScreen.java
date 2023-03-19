package com.limachi.lim_lib.screens;

import com.limachi.lim_lib.render.RenderUtils;
import com.limachi.lim_lib.widgets.BaseWidget;
import com.limachi.lim_lib.widgets.RootWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class WidgetContainerScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    public final RootWidget root;

    public WidgetContainerScreen(T menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        root = new RootWidget(this);
        addRenderableOnly(root);
    }

    @Override
    protected void init() {
        super.init();
        addRenderableOnly(root);
        root.init();
    }

    protected <W extends BaseWidget<?>> W addWidget(W widget) {
        root.addChild(widget);
        return widget;
    }

    protected <W extends BaseWidget<?>> W addRenderableWidget(W widget) {
        root.addChild(widget);
        return widget;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener widget) {
        super.setFocused(widget);
        if (widget != null)
            root.setFocused(null);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        root.backRenderPass(mouseX, mouseY);
        super.render(poseStack, mouseX, mouseY, partialTick);
        root.frontRenderPass(poseStack, mouseX, mouseY, partialTick);
        root.renderTooltips(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(@Nonnull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtils.background(this, poseStack);
    }

    @Override
    protected void containerTick() {
        root.containerTick();
        super.containerTick();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) { root.mouseMoved(mouseX, mouseY); }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return root.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return root.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) {
        return root.mouseDragged(mouseX, mouseY, button, fromX, fromY) || super.mouseDragged(mouseX, mouseY, button, fromX, fromY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return root.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean shouldCloseOnEsc() { return !root.catchEsc(); }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return root.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return root.keyReleased(keyCode, scanCode, modifiers) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return root.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
    }
}
