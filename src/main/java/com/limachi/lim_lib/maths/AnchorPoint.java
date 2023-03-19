package com.limachi.lim_lib.maths;

public record AnchorPoint(double anchorX, boolean fromRight, double anchorY, boolean fromBottom, boolean isFactor) {
    public static AnchorPoint TOP_LEFT = new AnchorPoint(0, false, 0, false, true);
    public static AnchorPoint TOP_RIGHT = new AnchorPoint(0, true, 0, false, true);
    public static AnchorPoint CENTER = new AnchorPoint(0.5, false, 0.5, false, true);
    public static AnchorPoint BOTTOM_LEFT = new AnchorPoint(0, false, 0, true, true);
    public static AnchorPoint BOTTOM_RIGHT = new AnchorPoint(0, true, 0, true, true);

    public IVec2i anchor(int width, int height) {
        double x;
        double y;
        if (isFactor) {
            x = fromRight ? width - width * anchorX : width * anchorX;
            y = fromBottom ? height - height * anchorX : height * anchorY;
        } else {
            x = fromRight ? width - anchorX : anchorX;
            y = fromBottom ? height - anchorY : anchorY;
        }
        return new IVec2i((int)x, (int)y);
    }
}