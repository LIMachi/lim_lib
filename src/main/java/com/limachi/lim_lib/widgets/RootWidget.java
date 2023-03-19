package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.AnchoredBox;
import com.limachi.lim_lib.data.IAreaUser;
import com.limachi.lim_lib.data.TreeNode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class RootWidget extends BaseWidget<RootWidget> implements Widget {
    @Nonnull
    public final AbstractContainerScreen<?> screen;
    private BaseWidget<?> focused = null;
    private double mouseX = Double.MIN_VALUE;
    private double mouseY = Double.MIN_VALUE;
    private int dragButton = -1;
    private LinkedList<Rect2i> scissors = new LinkedList<>();

    public void pushScissor(Rect2i box) {
        if (scissors.isEmpty())
            scissors.addFirst(box);
        else {
            box = new Rect2i(box.getX(), box.getY(), box.getWidth(), box.getHeight()).intersect(scissors.getFirst());
            scissors.addFirst(box);
        }
        GuiComponent.enableScissor(box.getX(), box.getY(), box.getX() + box.getWidth(), box.getY() + box.getHeight());
    }

    public void popScissor() {
        scissors.removeFirst();
        if (scissors.isEmpty())
            GuiComponent.disableScissor();
    }

    public void setDragButton(int button) {
        screen.setDragging(button >= GLFW.GLFW_MOUSE_BUTTON_1 && button <= GLFW.GLFW_MOUSE_BUTTON_LAST);
        dragButton = button;
    }
    public int getDragButton() { return screen.isDragging() ? dragButton : -1; }

    public double mouseX() { return mouseX; }
    public double mouseY() { return mouseY; }

    public int dragButton() { return dragButton; }

    public RootWidget(@Nonnull AbstractContainerScreen<?> screen) {
        super(AnchoredBox.topLeftDeltaBox(screen.getGuiLeft(), screen.getGuiTop(), screen.getXSize(), screen.getYSize()), new WidgetOptions().canAnimate(false));
        this.screen = screen;
        root();
    }

    public BaseWidget<?> getFocused() {
        if (screen.getFocused() == null)
            return focused;
        return null;
    }

    public void setFocused(BaseWidget<?> widget) {
        screen.setFocused(null);
        focused = widget;
    }

    protected boolean frontRenderPass = true;

    @FunctionalInterface
    public interface ScreenRenderMethod {
        void run(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick);
    }

    public void backRenderPass(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        frontRenderPass = false;
    }

    public void frontRenderPass(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        frontRenderPass = true;
        RenderSystem.disableDepthTest();
        render(poseStack, mouseX, mouseY, partialTick);
        RenderSystem.enableDepthTest();
    }

    public Pair<ItemStack, Optional<TooltipComponent>> gatherTooltips(List<Component> tooltip) {
        final ItemStack []stack = {ItemStack.EMPTY};
        final TooltipComponent []image = {null};
        Predicate<BaseWidget<?>> r = w->{
            List<Component> t = w.getTooltip();
            if (t != null)
                tooltip.addAll(t);
            if (stack[0].isEmpty())
                stack[0] = w.getTooltipStack();
            if (image[0] == null)
                image[0] = w.getTooltipImage();
            return false;
        };
        node.propagateDown(r, -1, false);
        r.test(node.getContent());
        return new Pair<>(stack[0], Optional.ofNullable(image[0]));
    }

    public void renderTooltips(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        List<Component> tooltip = new LinkedList<>();
        Pair<ItemStack, Optional<TooltipComponent>> t = gatherTooltips(tooltip);
        if (screen.getMenu().getCarried().isEmpty() && screen.getSlotUnderMouse() != null && screen.getSlotUnderMouse().hasItem()) {
            ItemStack stack = screen.getSlotUnderMouse().getItem();
            List<Component> itemTooltip = screen.getTooltipFromItem(stack);
            itemTooltip.addAll(tooltip);
            screen.renderTooltip(poseStack, itemTooltip, stack.getTooltipImage(), mouseX, mouseY, stack);
        } else if (tooltip.size() > 0)
            screen.renderTooltip(poseStack, tooltip, t.getSecond(), mouseX, mouseY, t.getFirst());
    }

    public void mouseMoved(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        node.propagateDown(w->{
            if (w.widgetOptions.catchMouseEvents()) {
                w.isMouseOver(mouseX, mouseY);
                w.onMouseMove();
            }
        }, -1, false);
    }

    private boolean runOnWidgets(Function<BaseWidget<?>, Boolean> run) {
        BaseWidget<?> prioritize = getFocused();
        return (prioritize != null && run.apply(prioritize)) || run.apply(this);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        return node.propagateDown(w->{
            w.isMouseOver(mouseX, mouseY);
            if (w.widgetOptions.catchMouseEvents() && w.onMouseClicked(mouseX, mouseY, button)) {
                if (w.isOvered && w.widgetOptions.canTakeFocus())
                    w.changeFocus(true);
                w.root().setDragButton(button);
                return true;
            }
            return false;
        }, -1, false);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        return node.propagateDown(w->{
            w.isMouseOver(mouseX, mouseY);
            if (!w.isOvered && w.widgetOptions.looseFocusOnClickOff())
                w.changeFocus(false);
            return w.widgetOptions.catchMouseEvents() && w.onMouseReleased(mouseX, mouseY, button);
        }, -1, false);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        return node.propagateDown(w->{
            w.isMouseOver(mouseX, mouseY);
            return w.widgetOptions.catchMouseEvents() && w.onMouseDragged(mouseX, mouseY, button, fromX, fromY);
        }, -1, false);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        return node.propagateDown(w->{
            w.isMouseOver(mouseX, mouseY);
            return w.widgetOptions.catchMouseEvents() && w.onMouseScrolled(mouseX, mouseY, amount);
        }, -1, false);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return node.propagateDown(w->w.widgetOptions.catchKeyboardEvents() && w.onKeyPressed(keyCode, scanCode, modifiers), -1, false);
    }

    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return node.propagateDown(w->w.widgetOptions.catchKeyboardEvents() && w.onKeyReleased(keyCode, scanCode, modifiers), -1, false);
    }

    public boolean charTyped(char codePoint, int modifiers) {
        return node.propagateDown(w->w.widgetOptions.catchKeyboardEvents() && w.onCharTyped(codePoint, modifiers), -1, false);
    }

    @Override
    public boolean changeFocus(boolean take) { return super.changeFocus(take); }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) { return super.isMouseOver(mouseX, mouseY); }

    public void init() {
        areaHandler.onlyRootShouldUseThis(screen);
        node.propagateDown((Consumer<BaseWidget<?>>)IAreaUser::updateArea, -1, true);
    }

    public void containerTick() { node.propagateDown((Consumer<BaseWidget<?>>)BaseWidget::tick, -1, true); }

    public boolean catchEsc() { return node.propagateDown((Predicate<BaseWidget<?>>)BaseWidget::canCatchEsc, -1, false); }
}
