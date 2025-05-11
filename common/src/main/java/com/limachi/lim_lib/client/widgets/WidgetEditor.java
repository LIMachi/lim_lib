package com.limachi.lim_lib.client.widgets;

import com.limachi.lim_lib.client.utils.GUI;
import com.limachi.lim_lib.common.math.Rect2d;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class WidgetEditor implements Renderable, GuiEventListener, LayoutElement, NarratableEntry {
    public int buttonWidth = 5;
    public int buttonHeight = 5;
    public boolean hovered = false;
    public boolean focused = false;
    public int selectedCorner = 0;
    public final AbstractWidget widget;
    public int selectedColor = 0xFF00FF00;
    public int hoveredColor = 0xAA00FFFF;
    public int defaultColor = 0x55555555;

    public WidgetEditor(AbstractWidget widget) { this.widget = widget; }

    public WidgetEditor() {
        this.widget = Button.builder(Component.empty(), b->{}).pos(200, 200).build();
    }

    @Override
    public final void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        hovered = isMouseOver(mouseX, mouseY);
        widget.render(guiGraphics, mouseX, mouseY, partialTick);
        int color = defaultColor;
        if (selectedCorner > 0)
            color = selectedColor;
        else if (hovered || focused)
            color = hoveredColor;
        GUI.emptySquare(guiGraphics, Rect2d.from(getRectangle()), color);
        ScreenRectangle corner = getPositionCorner();
        if (selectedCorner == 1)
            guiGraphics.fill(corner.left(), corner.top(), corner.right() , corner.bottom() , selectedColor);
        else
            GUI.emptySquare(guiGraphics, Rect2d.from(corner), defaultColor);
        corner = getWidthCorner ();
        if (selectedCorner == 2)
            guiGraphics.fill(corner.left(), corner.top(), corner.right() , corner.bottom() , selectedColor);
        else
            GUI.emptySquare(guiGraphics, Rect2d.from(corner), defaultColor);
        corner = getHeightCorner();
        if (selectedCorner == 3)
            guiGraphics.fill(corner.left(), corner.top(), corner.right() , corner.bottom() , selectedColor);
        else
            GUI.emptySquare(guiGraphics, Rect2d.from(corner), defaultColor);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int ix = Math.round((float)mouseX);
        int iy = Math.round((float)mouseY);
        selectedCorner = 0;
        if (getPositionCorner().containsPoint(ix, iy))
            selectedCorner = 1;
        else if (getWidthCorner().containsPoint(ix, iy))
            selectedCorner = 2;
        else if (getHeightCorner().containsPoint(ix, iy))
            selectedCorner = 3;
        else if (getRectangle().containsPoint(ix, iy))
            hovered = true;
        if (selectedCorner > 0 || hovered) {
            setFocused(true);
            return true;
        } else {
            setFocused(false);
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double mouseDeltaX, double mouseDeltaY) {
        return false;
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        return !isFocused() ? ComponentPath.leaf(this) : null;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getRectangle().containsPoint(Math.round((float)mouseX), Math.round((float)mouseY));
    }

    @Override
    public void setFocused(boolean state) { focused = state; }

    @Override
    public boolean isFocused() { return focused; }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        if (this.isFocused()) {
            return NarrationPriority.FOCUSED;
        } else {
            return this.hovered ? NarrationPriority.HOVERED : NarrationPriority.NONE;
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public int getWidth() { return widget.getWidth(); }

    @Override
    public int getHeight() { return widget.getHeight(); }

    @Override
    public int getX() { return widget.getX(); }

    @Override
    public void setX(int x) { widget.setX(x); }

    @Override
    public int getY() { return widget.getY(); }

    @Override
    public void setY(int y) { widget.setY(y); }

    public ScreenRectangle getPositionCorner() {
        return new ScreenRectangle(getX() - buttonWidth, getY() - buttonHeight, buttonWidth, buttonHeight);
    }

    public ScreenRectangle getWidthCorner() {
        return new ScreenRectangle(getX() + getWidth(), getY() - buttonHeight, buttonWidth, buttonHeight);
    }

    public ScreenRectangle getHeightCorner() {
        return new ScreenRectangle(getX() - buttonWidth, getY() + getHeight(), buttonWidth, buttonHeight);
    }

    @Override
    public ScreenRectangle getRectangle() { return widget.getRectangle(); }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {}
}