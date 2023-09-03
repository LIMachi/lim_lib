package com.limachi.lim_lib.widgetsOld;

/*
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
        updateArea();
    }

    protected BaseWidget(@Nonnull AnchoredBox box, @Nonnull WidgetOptions options) {
        areaHandler = new Area(box, this::parentAreaGetter);
        widgetOptions = options;
        updateArea();
    }

    protected Area parentAreaGetter() { return node.safeParentCall(p->p.getContent().areaHandler, null); }

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
        if (widgetOptions.active()) {
            boolean shouldAdd = true;
            for (Rect2i u : usage)
                if (currentArea().containedIn(u)) {
                    shouldAdd = false;
                    break;
                }
            if (shouldAdd)
                usage.add(new Rect2i((int) currentArea().getX1(), (int) currentArea().getY1(), (int) currentArea().getWidth(), (int) currentArea().getHeight()));
            node.propagateDown(n -> n.getContent().gatherScreenUsage(usage), true, -1);
        }
        return false;
    }

    protected void onAreaResize() {}

    public void tick() {}

    protected T setBackground(@Nullable ResourceLocation backgroundTexture, Box2d backgroundCutout) {
        this.backgroundTexture = backgroundTexture;
        this.backgroundCutout = backgroundCutout;
        return (T)this;
    }

    public void backRender(@Nonnull PoseStack stack, double mouseX, double mouseY, float partialTick) {}

    public void frontRender(@Nonnull PoseStack stack, double mouseX, double mouseY, float partialTick) {}

    @Override
    public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (widgetOptions.active()) {
            if (!root().frontRenderPass) {
                partialTick(partialTick);
                if (widgetOptions.shouldRender()) {
                    if (backgroundTexture != null) {
                        RenderSystem.setShaderTexture(0, backgroundTexture);
                        RenderUtils.blitMiddleExp(null, stack, 0, currentArea(), backgroundCutout);
                    }
                    backRender(stack, mouseX, mouseY, partialTick);
                }
            }
            if (widgetOptions.scissorRender())
                root().pushScissor(currentArea().asRect());
            if (widgetOptions.shouldRenderChildren())
                node.propagateDown(n -> {
                    n.getContent().render(stack, mouseX, mouseY, partialTick);
                    return false;
                }, false, 1);
            if (root().frontRenderPass && widgetOptions.shouldRender())
                frontRender(stack, mouseX, mouseY, partialTick);
            if (widgetOptions.scissorRender())
                root().popScissor();
        }
    }

    @Nullable
    protected List<Component> getTooltip() { return isOvered && widgetOptions.active() ? defaultTooltip : null; }
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

    public boolean isFocused() { return widgetOptions.active() && root().getFocused() == this; }

    public boolean isDragged() { return isFocused() && screen().isDragging(); }

    public final boolean isOvered() { return widgetOptions.active() && isOvered; }

    protected boolean changeFocus(boolean take) {
        if (widgetOptions.active()) {
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
        }
        return false;
    }

    protected boolean isMouseOver(double mouseX, double mouseY) {
        if (!widgetOptions.active()) return false;
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
*/