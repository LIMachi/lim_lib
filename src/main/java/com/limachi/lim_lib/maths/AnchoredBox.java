package com.limachi.lim_lib.maths;

public class AnchoredBox {

    protected AnchorPoint parentAnchor;
    protected AnchorPoint localAnchor;
    protected IVec2i area;
    protected boolean fill;

    public AnchoredBox(AnchorPoint p, int w, int h, AnchorPoint l) {
        area = new IVec2i(w, h);
        parentAnchor = p;
        localAnchor = l;
        fill = false;
    }

    /**
     * same as anchored box with width and height, but instead fill as much of the parent box as possible
     */
    public AnchoredBox(AnchorPoint p, AnchorPoint l) {
        area = new IVec2i(0, 0);
        parentAnchor = p;
        localAnchor = l;
        fill = true;
    }

    /**
     * Will create a box where the top left of the box is aligned with an offset pixel from the parent top left corner.
     * If the parent is the entire screen, this will be equivalent to new Box2i(x, y, w, h).
     */
    public static AnchoredBox topLeftDeltaBox(int x, int y, int w, int h) {
        return new AnchoredBox(new AnchorPoint(x, false, y, false, false), w, h, AnchorPoint.TOP_LEFT);
    }

    /**
     * Will create a box perfectly centered on the parent box (clamping is disabled by default).
     */
    public static AnchoredBox centeredBox(int w, int h) {
        return new AnchoredBox(AnchorPoint.CENTER, w, h, AnchorPoint.CENTER);
    }

    public static AnchoredBox fill() {
        return new AnchoredBox(AnchorPoint.CENTER, AnchorPoint.CENTER);
    }

    public static AnchoredBox topRightBox(int w, int h) {
        return new AnchoredBox(AnchorPoint.TOP_RIGHT, w, h, AnchorPoint.TOP_RIGHT);
    }

    public static AnchoredBox bottomRightBox(int w, int h) {
        return new AnchoredBox(AnchorPoint.BOTTOM_RIGHT, w, h, AnchorPoint.BOTTOM_RIGHT);
    }

    public static AnchoredBox bottomLeftBox(int w, int h) {
        return new AnchoredBox(AnchorPoint.BOTTOM_LEFT, w, h, AnchorPoint.BOTTOM_LEFT);
    }

    public boolean isBottomBound() {
        return (parentAnchor.equals(AnchorPoint.BOTTOM_LEFT) && localAnchor.equals(AnchorPoint.BOTTOM_LEFT)) ||
                (parentAnchor.equals(AnchorPoint.BOTTOM_RIGHT) && localAnchor.equals(AnchorPoint.BOTTOM_RIGHT));
    }

    public AnchoredBox copy() { return fill ? new AnchoredBox(parentAnchor, localAnchor) : new AnchoredBox(parentAnchor, area.x(), area.y(), localAnchor); }

    /**
     * Calculate the delta that should be applied to a pixel to be translated from "parent" referential to "local" referential.
     * Since this is only a 2d vector representing a "correction", you can simply inverse-it (by subtracting) to revert the transformation from "parent" to "local".
     * If clamp is active, will make sure the delta applied to origin (0, 0: top left) will not be outside the parentArea.
     */
    public IVec2i delta(IVec2i parentArea) {
        if (fill)
            area = parentArea.copy();
        return parentAnchor.anchor(parentArea.x(), parentArea.y()).sub(localAnchor.anchor(area.x(), area.y()));
    }

    public int width() { return area.x(); }
    public int height() { return area.y(); }
    public IVec2i area() { return area; }
    public AnchorPoint parentAnchor() { return parentAnchor; }
    public AnchorPoint localAnchor() { return localAnchor; }

    public AnchoredBox resize(int w, int h) {
        if (!fill && (w != area.x() || h != area.y())) {
            area = new IVec2i(w, h);
        }
        return this;
    }

    public AnchoredBox setLocalAnchor(AnchorPoint anchor) {
        if (!localAnchor.equals(anchor)) {
            localAnchor = anchor;
        }
        return this;
    }

    public AnchoredBox setParentAnchor(AnchorPoint anchor) {
        if (!parentAnchor.equals(anchor)) {
            parentAnchor = anchor;
        }
        return this;
    }

    public AnchoredBox setFilling(boolean state) {
        fill = state;
        return this;
    }
}
