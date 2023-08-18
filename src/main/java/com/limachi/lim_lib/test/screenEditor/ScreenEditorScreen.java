package com.limachi.lim_lib.test.screenEditor;

/**
 * key base actions:
 * ins -> new widget (auto selected)        ok
 * del -> delete widget (selected)          ok
 * tab (shift) -> select next/prev widget   ok
 * arrows -> move widget                    ok
 * arrows + ctrl -> expand/shrink widget    ok
 * b -> toggle widget background            ok
 * e -> text edit widget title              ok
 * i -> text edit widget image location
 * c -> edit widget image cutout
 * f1 -> toggle between test/edit mode
 * + -> reorder selected widget to render above/catch events before     ok
 * - -> reorder selected widget to render below/catch events after      ok
 *
 * mouse actions:
 * left click: select (use hitbox from most recent to oldest)   ok
 * left drag: move/resize                                       ok
 */

/*
@RegisterMenuScreen(skip = "com.limachi.lim_lib.LimLib:useTests")
public class ScreenEditorScreen extends AbstractContainerScreen<ScreenEditorMenu> {

    protected static class PseudoWidget {
        Box2d area;
        boolean background;
        String title;
        ResourceLocation texture = null;
        Box2d cutout = null;
        int renderType = 0;

        PseudoWidget(int x, int y) {
            area = new Box2d(x, y, 16, 16);
            background = true;
            title = "";
        }

        void render(@NotNull PoseStack poseStack, float partialTick, int mouseX, int mouseY, ScreenEditorScreen screen) {
            if (background) {
                RenderSystem.setShaderTexture(0, RenderUtils.BACKGROUND_TEXTURE);
                RenderUtils.blitMiddleExp(null, poseStack, 0, area, null, RenderUtils.DEFAULT_FILE_WIDTH, RenderUtils.DEFAULT_FILE_HEIGHT);
            }
            if (texture != null) {
                RenderSystem.setShaderTexture(0, texture);
                Box2d c = cutout == null ? area : cutout;
                if (renderType == 0)
                    RenderUtils.blitMiddleExp(null, poseStack, 0, c, null, RenderUtils.DEFAULT_FILE_WIDTH, RenderUtils.DEFAULT_FILE_HEIGHT);
                else if (renderType == 1)
                    RenderUtils.blitUnscaled(null, poseStack, 0, c, null, RenderUtils.DEFAULT_FILE_WIDTH, RenderUtils.DEFAULT_FILE_HEIGHT);
            }
            if (!title.isBlank())
                RenderUtils.drawString(poseStack, Minecraft.getInstance().font, title, area, 0xFFFFFFFF, true, true);
        }

        PseudoWidget setBackground(boolean state) {
            background = state;
            return this;
        }

        PseudoWidget setTitle(String title) {
            if (title == null)
                this.title = "";
            else
                this.title = title;
            return this;
        }

        PseudoWidget moveDelta(int x, int y) {
            area.move(x, y);
            return this;
        }

        PseudoWidget expand(int w, int h) {
            area.expand(w, h);
            return this;
        }

        PseudoWidget setWidthHeight(int w, int h) {
            area.setWidth(w).setHeight(h);
            return this;
        }
    }

    protected int focused = -1;
    protected final ArrayList<PseudoWidget> widgets = new ArrayList<>();
    protected int dragCorner = 0;
    protected EditBox textEdit = new EditBox(Minecraft.getInstance().font, 0, 0, 0, 0, Component.empty());

    public ScreenEditorScreen(ScreenEditorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        textEdit.setEditable(true);
        textEdit.changeFocus(true);
        textEdit.setVisible(false);
        textEdit.setMaxLength(256);
        textEdit.setHeight(16);
        widgets.add(new PseudoWidget(0, 0).setWidthHeight(176, 166));
        widgets.add(new PseudoWidget(titleLabelX, titleLabelY).setTitle(title.getString()).setBackground(false));
        widgets.add(new PseudoWidget(inventoryLabelX, inventoryLabelY).setTitle(playerInventoryTitle.getString()).setBackground(false));
    }

    protected boolean isFocused(PseudoWidget widget) {
        return focused >= 0 && focused < widgets.size() && widget == widgets.get(focused);
    }

    protected void cycleFocus(int steps) {
        if (widgets.size() == 0) {
            focused = -1;
            return;
        }
        int t = focused + 1;
        t += steps;
        while (t > widgets.size())
            t -= widgets.size() + 1;
        while (t < 0)
            t += widgets.size() + 1;
        focused = t - 1;
    }

    protected void bubbleFocused(boolean up) {
        if (up) {
            if (focused >= 0 && focused < widgets.size() - 1) {
                PseudoWidget t = widgets.get(focused);
                widgets.set(focused, widgets.get(focused + 1));
                widgets.set(++focused, t);
            }
        } else {
            if (focused > 0 && focused < widgets.size()) {
                PseudoWidget t = widgets.get(focused);
                widgets.set(focused, widgets.get(focused - 1));
                widgets.set(--focused, t);
            }
        }
    }

    protected void setupTextEdit(String str, boolean background, Consumer<String> response) {
        textEdit.setFocus(true);
        textEdit.setVisible(true);
        PseudoWidget w = widgets.get(focused);
        w.setBackground(background);
        textEdit.setMessage(Component.literal(str));
        textEdit.x = (int)w.area.getX1();
        textEdit.y = (int)w.area.getY1();
        textEdit.setWidth(Integer.max((int)w.area.getWidth(), 32));
        textEdit.setResponder(response);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (textEdit.canConsumeInput())
            return textEdit.keyPressed(keyCode, scanCode, modifiers);
        switch (keyCode) {
            case GLFW.GLFW_KEY_E -> {
                if (focused >= 0) {
                    setupTextEdit(widgets.get(focused).title, false, s->widgets.get(focused).title = s);
                    return true;
                }
            }
            case GLFW.GLFW_KEY_I -> {
                if (focused >= 0) {
                    setupTextEdit("", true, s->widgets.get(focused).texture = s.isBlank() ? null : new ResourceLocation(s));
                    return true;
                }
            }
            case GLFW.GLFW_KEY_TAB -> {
                if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0)
                    cycleFocus(-1);
                else
                    cycleFocus(1);
                return true;
            }
            case GLFW.GLFW_KEY_KP_ADD -> {
                bubbleFocused(true);
                return true;
            }
            case GLFW.GLFW_KEY_KP_SUBTRACT -> {
                bubbleFocused(false);
                return true;
            }
            case GLFW.GLFW_KEY_INSERT -> {
                widgets.add(new PseudoWidget(0, 0));
                focused = widgets.size() - 1;
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (focused > 0) {
                    widgets.remove(focused);
                    focused = -1;
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_DOWN -> {
                if (focused >= 0) {
                    if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0)
                        widgets.get(focused).expand(keyCode == GLFW.GLFW_KEY_RIGHT ? 1 : keyCode == GLFW.GLFW_KEY_LEFT ? -1 : 0, keyCode == GLFW.GLFW_KEY_DOWN ? 1 : keyCode == GLFW.GLFW_KEY_UP ? -1 : 0);
                    else
                        widgets.get(focused).moveDelta(keyCode == GLFW.GLFW_KEY_RIGHT ? 1 : keyCode == GLFW.GLFW_KEY_LEFT ? -1 : 0, keyCode == GLFW.GLFW_KEY_DOWN ? 1 : keyCode == GLFW.GLFW_KEY_UP ? -1 : 0);
                }
                return true;
            }
            case GLFW.GLFW_KEY_B -> {
                if (focused >= 0)
                    widgets.get(focused).setBackground(!widgets.get(focused).background);
                return true;
            }
            case GLFW.GLFW_KEY_ESCAPE -> {
                if (focused >= 0) {
                    focused = -1;
                    return true;
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (textEdit.canConsumeInput())
            return textEdit.charTyped(character, modifiers);
        return super.charTyped(character, modifiers);
    }

    @Override
    protected void containerTick() {
        textEdit.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mx = mouseX - getGuiLeft();
        double my = mouseY - getGuiTop();
        if (textEdit.canConsumeInput())
            return textEdit.mouseClicked(mx, my, button);
        dragCorner = 0;
        for (int i = widgets.size() - 1; i >= 0; --i)
            if (widgets.get(i).area.isIn(mx, my)) {
                focused = i;
                Box2d test = widgets.get(i).area.copy().scaleWidthAndHeight(0.2, 0.2);
                if (test.isIn(mx, my))
                    dragCorner = 1;
                if (test.move(test.getWidth() * 4, 0).isIn(mx, my))
                    dragCorner = 2;
                if (test.move(0, test.getHeight() * 4).isIn(mx, my))
                    dragCorner = 3;
                if (test.move(-test.getWidth() * 4, 0).isIn(mx, my))
                    dragCorner = 4;
                return true;
            }
        focused = -1;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double fromX, double fromY) {
        double mx = mouseX - getGuiLeft();
        double my = mouseY - getGuiTop();
        if (textEdit.canConsumeInput())
            return textEdit.mouseDragged(mx, my, button, fromX - getGuiLeft(), fromY - getGuiTop());
        if (focused >= 0)
            if (dragCorner == 1)
                widgets.get(focused).area.setX1(mx).setY1(my);
            else if (dragCorner == 2)
                widgets.get(focused).area.setX2(mx).setY1(my);
            else if (dragCorner == 3)
                widgets.get(focused).area.setX2(mx).setY2(my);
            else if (dragCorner == 4)
                widgets.get(focused).area.setX1(mx).setY2(my);
            else {
                Box2d area = widgets.get(focused).area;
                area.setX1(mx - area.getWidth() / 2).setY1(my - area.getHeight() / 2);
            }
        return super.mouseDragged(mouseX, mouseY, button, fromX, fromY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double mx = mouseX - getGuiLeft();
        double my = mouseY - getGuiTop();
        if (textEdit.canConsumeInput())
            return textEdit.mouseReleased(mx, my, button);
        dragCorner = 0;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        renderBackground(poseStack);
        poseStack.pushPose();
        poseStack.translate(getGuiLeft(), getGuiTop(), 1.);
        for (PseudoWidget w : widgets)
            w.render(poseStack, partialTick, mouseX, mouseY, this);
        if (focused >= 0)
            RenderUtils.drawBox(poseStack, widgets.get(focused).area, 0x2222FF55, 0);
        if (textEdit.canConsumeInput())
            textEdit.renderButton(poseStack, mouseX, mouseY, partialTick);
        poseStack.popPose();
    }

    @Override
    protected void renderLabels(PoseStack stack, int x, int y) {}
}
*/