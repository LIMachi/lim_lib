package com.limachi.lim_lib.widgetsOld;

/*
@SuppressWarnings({"unused", "UnusedReturnValue", "unchecked"})
@OnlyIn(Dist.CLIENT)
public abstract class BaseWidget<T extends BaseWidget<T>> extends AbstractContainerEventHandler implements Widget, NarratableEntry {

    protected Box2d area;
    private AbstractContainerScreen<?> screen = null;
    protected ResourceLocation backgroundTexture;
    protected Box2d backgroundCutout;
    protected List<? extends GuiEventListener> children = new ArrayList<>();

    protected final ArrayList<Component> tooltip = new ArrayList<>();
    private boolean simpleBlit;

    public BaseWidget(int x, int y, int w, int h, ResourceLocation texture, Box2d cutout, boolean simpleBlit) {
        area = new Box2d(x, y, w, h);
        backgroundTexture = texture;
        backgroundCutout = cutout;
        this.simpleBlit = simpleBlit;
    }

    public T attachToScreen(AbstractContainerScreen<?> containerScreen) {
        if (containerScreen instanceof ILimLibScreen)
            screen = containerScreen;
        else
            Log.error(containerScreen, "invalid screen type attached, must implement ILimLibScreen");
        return (T)this;
    }

    public AbstractContainerScreen<?> getScreen() { return screen; }

    public Box2d getArea() { return area; }

    public T moveTo(int x, int y) {
        area.setX1(x).setY1(y);
        return (T)this;
    }

    public T rescale(int width, int height) {
        area.setWidth(width).setHeight(height);
        return (T)this;
    }

    public T setBackground(ResourceLocation texture, Box2d cutout) {
        backgroundTexture = texture;
        backgroundCutout = cutout;
        simpleBlit = false;
        return (T)this;
    }

    public T setBackground(ResourceLocation texture, Vec2 origin) {
        backgroundTexture = texture;
        backgroundCutout.setX1(origin.x).setY1(origin.y);
        simpleBlit = true;
        return (T)this;
    }

    public T addTooltip(Collection<Component> tooltipAddition) {
        tooltip.addAll(tooltipAddition);
        return (T)this;
    }

    public T addTooltip(Component ... tooltipAddition) {
        tooltip.addAll(Arrays.stream(tooltipAddition).toList());
        return (T)this;
    }

    public T clearTooltip() {
        tooltip.clear();
        return (T)this;
    }

    public ArrayList<Component> getTooltip() {
        return tooltip;
    }

    @Override
    public void render(@Nonnull PoseStack stack, int mouseX, int mouseY, float partialTick) {
        if (screen != null) {
            RenderSystem.setShaderTexture(0, backgroundTexture);
            if (simpleBlit)
                RenderUtils.blitUnscaled(screen, stack, getBlitOffset(), area, backgroundCutout);
            else
                RenderUtils.blitMiddleExp(screen, stack, getBlitOffset(), area, backgroundCutout);
            boolean isMouseOver = isMouseOver(mouseX, mouseY);
            stack.pushPose();
            stack.translate(screen.getGuiLeft() + area.getX1(), screen.getGuiTop() + area.getY1(), 0);
            renderRelative(stack, relativeMouseOverX(mouseX), relativeMouseOverY(mouseY), partialTick, isMouseOver);
            stack.popPose();
            if (isMouseOver && tooltip.size() > 0 && screen instanceof ILimLibScreen s)
                s.overrideTooltip(tooltip);
        }
    }

    public void renderRelative(PoseStack stack, int mouseX, int mouseY, float partialTick, boolean isMouseOver) {}

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isMouseOverBox(mouseX, mouseY, area);
    }

    public boolean isMouseOverBox(double mouseX, double mouseY, Box2d box) {
        return screen != null && screen.getGuiLeft() + box.getX1() <= mouseX && mouseX < screen.getGuiLeft() + box.getX2() && screen.getGuiTop() + box.getY1() <= mouseY && mouseY < screen.getGuiTop() + box.getY2(); //fixed minor overlap issue due to check including 1 more pixel than expected
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
    @Nonnull
    public List<? extends GuiEventListener> children() { return children; }

    @Override
    @Nonnull
    public NarrationPriority narrationPriority() { return NarratableEntry.NarrationPriority.NONE; }

    @Override
    public void updateNarration(@Nonnull NarrationElementOutput output) {}

    public T focus(boolean takeFocus) {
        if (screen != null)
            screen.setFocused(takeFocus ? this : null);
        return (T)this;
    }

    public boolean shouldCloseOnEsc() { return true; }
}
*/