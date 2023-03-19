package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.*;
import com.limachi.lim_lib.render.RenderUtils;
import com.limachi.lim_lib.data.IAreaUser;
import com.limachi.lim_lib.data.TreeNode;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseWidget<T extends BaseWidget<T>> implements IAreaUser, Widget {

    @Nullable
    protected ResourceLocation backgroundTexture = null;
    protected Box2d backgroundCutout = new Box2d(0, 0);

    protected final TreeNode<BaseWidget<?>> node = new TreeNode<>(this);

    private RootWidget root = null;

    @Nullable
    protected BaseWidget<?> previousFocus = null;

    protected boolean isOvered = false;

    protected WidgetOptions widgetOptions;

    protected final Area areaHandler;

    protected List<Component> defaultTooltip;

    protected BaseWidget(@Nonnull Area areaHandler, @Nonnull WidgetOptions options) {
        this.areaHandler = areaHandler;
        widgetOptions = options;
    }

    protected BaseWidget(@Nonnull AnchoredBox box, @Nonnull WidgetOptions options) {
        areaHandler = new Area(box, ()->node.safeParentCall(p->p.getContent().areaHandler, null));
        widgetOptions = options;
    }

    protected AbstractContainerScreen<?> screen() { return root != null ? root.screen : null; }

    protected RootWidget root() {
        if (root != null) return root;
        return root = (node.getRoot().getContent() instanceof RootWidget w ? w : null);
    }

    protected BaseWidget<?> parent() { return node.safeParentCall(TreeNode::getContent, null); }

    @Override
    public Area areaHandler() { return areaHandler; }

    public BaseWidget<T> addDefaultTooltip(List<Component> components) {
        this.defaultTooltip = components;
        return this;
    }

    public BaseWidget<T> addDefaultTooltip(Component ... components) {
        return addDefaultTooltip(Arrays.stream(components).toList());
    }

    public BaseWidget<T> addChild(BaseWidget<?> widget) {
        node.addChild(widget.node);
        widget.updateArea();
        return this;
    }

    public boolean gatherScreenUsage(ArrayList<Rect2i> usage) {
        boolean shouldAdd = true;
        for (Rect2i u : usage)
            if (currentArea().containedIn(u)) {
                shouldAdd = false;
                break;
            }
        if (shouldAdd)
            usage.add(new Rect2i((int)currentArea().getX1(), (int)currentArea().getY1(), (int)currentArea().getWidth(), (int)currentArea().getHeight()));
        node.propagateDown(n->n.getContent().gatherScreenUsage(usage), true, -1);
        return false;
    }

    protected void onAreaResize() {}

    public void tick() {}

    protected T setBackground(@Nullable ResourceLocation backgroundTexture, Box2d backgroundCutout) {
        this.backgroundTexture = backgroundTexture;
        this.backgroundCutout = backgroundCutout;
        return (T)this;
    }

    /**
     * Will be called after the backgrounds (of the screen and self) have been rendered, before children render.
     * Use it if the background render is not enough (animation, object render, etc...).
     */
    public void backRender(@Nonnull PoseStack stack, double mouseX, double mouseY, float partialTick) {}

    /**
     * Will be called at the VERY END of the screen render (but before tooltip rendering), in inverse order (top widget last).
     * Used for overlays/transparent textures.
     */
    public void frontRender(@Nonnull PoseStack stack, double mouseX, double mouseY, float partialTick) {}

    @Override
    public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (!root().frontRenderPass) {
            partialTick(partialTick);
            if (widgetOptions.shouldRender()) {
                if (backgroundTexture != null) {
                    RenderSystem.setShaderTexture(0, backgroundTexture);
                    RenderUtils.blitMiddleExp(null, stack, 0, currentArea(), backgroundCutout);
                }
                backRender(stack, mouseX, mouseY, partialTick);
            }
        } else if (widgetOptions.scissorRender())
            root().pushScissor(currentArea().asRect());
        if (widgetOptions.shouldRenderChildren())
            node.propagateDown(n->{n.getContent().render(stack, mouseX, mouseY, partialTick); return false;}, false, 1);
        if (root().frontRenderPass && widgetOptions.shouldRender())
            frontRender(stack, mouseX, mouseY, partialTick);
        if (widgetOptions.scissorRender())
            root().popScissor();
    }

    @Nullable
    protected List<Component> getTooltip() { return isOvered ? defaultTooltip : null; }
    protected ItemStack getTooltipStack() { return ItemStack.EMPTY; }
    @Nullable
    protected TooltipComponent getTooltipImage() { return null; }

    protected void onMouseStartOver(double mouseX, double mouseY) {}

    protected void onMouseStopOver(double mouseX, double mouseY) {}

    protected void onMouseMove() {}

    protected boolean onMouseClicked(double mouseX, double mouseY, int button) { return false; }

    protected boolean onMouseReleased(double mouseX, double mouseY, int button) { return false; }

    protected boolean onMouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) { return false; }

    protected boolean onMouseScrolled(double mouseX, double mouseY, double amount) { return false; }

    protected boolean onKeyPressed(int keyCode, int scanCode, int modifiers) { return false; }

    protected boolean onKeyReleased(int keyCode, int scanCode, int modifiers) { return false; }

    protected boolean onCharTyped(char codePoint, int modifiers) { return false; }

    public boolean isFocused() { return root().getFocused() == this; }

    public boolean isDragged() { return isFocused() && screen().isDragging(); }

    public final boolean isOvered() { return isOvered; }

    protected boolean changeFocus(boolean take) {
        BaseWidget<?> t = root().getFocused();
        if (take) {
            if (t == this)
                return true;
            if (t != null)
                previousFocus = t;
            root().setFocused(this);
            return true;
        }
        if (t == this)
            root().setFocused(previousFocus);
        return false;
    }

    protected boolean isMouseOver(double mouseX, double mouseY) {
        boolean out = currentArea().isIn(mouseX, mouseY);
        if (out && !isOvered) {
            isOvered = true;
            if (widgetOptions.catchMouseEvents())
                onMouseStartOver(mouseX, mouseY);
        } else if (!out && isOvered) {
            isOvered = false;
            if (widgetOptions.catchMouseEvents())
                onMouseStopOver(mouseX, mouseY);
        }
        return out;
    }

    public double relativeMouseX() { return root().mouseX() - currentArea().getX1(); }

    public double relativeMouseY() { return root().mouseY() - currentArea().getY1(); }

    public AnchorPoint parentAnchorPointUnderMouse() {
        Box2d parentArea = node.safeParentCall(n->n.getContent().currentArea(), null);
        if (parentArea == null)
            return AnchorPoint.CENTER;
        return new AnchorPoint((root().mouseX() - parentArea.getX1()) / parentArea.getWidth(), false, (root().mouseY() - parentArea.getY1()) / parentArea.getHeight(), false, true);
    }

    protected boolean canCatchEsc() { return false; }
}
