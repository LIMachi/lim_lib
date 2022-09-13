package com.limachi.lim_lib.widgets;

import com.limachi.lim_lib.maths.Box2d;
import com.limachi.lim_lib.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseWidget extends AbstractContainerEventHandler implements Widget, NarratableEntry {

    protected Box2d area;
    private AbstractContainerScreen<?> screen = null;
    private ResourceLocation backgroundTexture;
    private Box2d backgroundCutout;
    protected List<? extends GuiEventListener> children = new ArrayList<>();
    private boolean simpleBlit;

    public BaseWidget(int x, int y, int w, int h, ResourceLocation texture, Box2d cutout, boolean simpleBlit) {
        area = new Box2d(x, y, w, h);
        backgroundTexture = texture;
        backgroundCutout = cutout;
        this.simpleBlit = simpleBlit;
    }

    public void attachToScreen(AbstractContainerScreen<?> containerScreen) {
        screen = containerScreen;
    }

    public AbstractContainerScreen<?> getScreen() { return screen; }

    public Box2d getArea() { return area; }

    public <T extends BaseWidget> T moveTo(int x, int y) {
        area.setX1(x).setY1(y);
        return (T)this;
    }

    public <T extends BaseWidget> T rescale(int width, int height) {
        area.setWidth(width).setHeight(height);
        return (T)this;
    }

    public <T extends BaseWidget> T setBackground(ResourceLocation texture, Box2d cutout) {
        backgroundTexture = texture;
        backgroundCutout = cutout;
        simpleBlit = false;
        return (T)this;
    }

    public <T extends BaseWidget> T setBackground(ResourceLocation texture, Vec2 origin) {
        backgroundTexture = texture;
        backgroundCutout.setX1(origin.x).setY1(origin.y);
        simpleBlit = true;
        return (T)this;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (screen != null) {
            RenderSystem.setShaderTexture(0, backgroundTexture);
            if (simpleBlit)
                RenderUtils.blitUnscaled(screen, stack, getBlitOffset(), area, backgroundCutout.getOrigins());
            else
                RenderUtils.blitMiddleExp(screen, stack, getBlitOffset(), area, backgroundCutout);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isMouseOverBox(mouseX, mouseY, area);
    }

    public boolean isMouseOverBox(double mouseX, double mouseY, Box2d box) {
        return screen != null && screen.getGuiLeft() + box.getX1() <= mouseX && mouseX <= screen.getGuiLeft() + box.getX2() && screen.getGuiTop() + box.getY1() <= mouseY && mouseY <= screen.getGuiTop() + box.getY2();
    }

    public boolean isFocused() { return screen != null && screen.getFocused() == this; }
    public boolean isDragged() { return isFocused() && screen.isDragging(); }

    public int relativeMouseOverX(double mouseX) {
        if (screen == null) return Integer.MIN_VALUE;
        return (int)(mouseX - (screen.getGuiLeft() + area.getX1()));
    }

    public int relativeMouseOverY(double mouseY) {
        if (screen == null) return Integer.MIN_VALUE;
        return (int)(mouseY - (screen.getGuiTop() + area.getY1()));
    }

    public double fractionalMouseOverX(double mouseX) { return relativeMouseOverX(mouseX) / area.getWidth(); }

    public double fractionalMouseOverY(double mouseY) { return relativeMouseOverY(mouseY) / area.getHeight(); }

    @Override
    public List<? extends GuiEventListener> children() { return children; }

    @Override
    public NarrationPriority narrationPriority() { return NarratableEntry.NarrationPriority.NONE; }

    @Override
    public void updateNarration(NarrationElementOutput output) {}
}
