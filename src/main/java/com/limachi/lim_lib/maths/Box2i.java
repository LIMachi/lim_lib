package com.limachi.lim_lib.maths;

import com.mojang.math.MatrixUtil;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class Box2i {
    private int x;
    private int y;
    private int w;
    private int h;

    public Box2i(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        w = width;
        h = height;
    }

    public Box2i(int width, int height) {
        x = 0;
        y = 0;
        w = width;
        h = height;
    }

    public Box2i(IVec2i volume) {
        x = 0;
        y = 0;
        w = volume.x();
        h = volume.y();
    }

    public Box2i(IVec2i position, IVec2i volume) {
        x = position.x();
        y = position.y();
        w = volume.x();
        h = volume.y();
    }

    public IVec2i getOrigins() { return new IVec2i(x, y); }
    public IVec2i getArea() { return new IVec2i(w, h); }

    public static Box2i fromCorners(int x1, int y1, int x2, int y2) { return new Box2i(x1, y1,x2 - x1, y2 - y1); }

    public static Box2i fromCorners(IVec2i topLeft, IVec2i bottomRight) { return fromCorners(topLeft.x(), topLeft.y(), bottomRight.x(), bottomRight.y()); }

    public Box2i centerOn(int cx, int cy) { return setX1(cx - getWidth() / 2).setY1(cy - getHeight() / 2); }

    public Box2i expandToContain(int px, int py) { return expandToContain(px, py, 0, 0); }

    /**
     * if the given point is outside the box, expand the box so the point fits inside
     */

    public Box2i expandToContain(int px, int py, int xMargin, int yMargin) {
        if (px - xMargin < x) {
            w += x - px + xMargin;
            x = px - xMargin;
        }
        else if (px + xMargin > getX2())
            setX2(px + xMargin);
        if (py - yMargin < y) {
            h += y - py + yMargin;
            y = py - yMargin;
        }
        else if (py + yMargin > getY2())
            setY2(py + yMargin);
        return this;
    }

    public Box2i expandToContain(Box2i other, int xMargin, int yMargin) { return expandToContain(other.x, other.y, xMargin, yMargin).expandToContain(other.getX2(), other.getY2(), xMargin, yMargin); }

    public Box2i transform(Matrix4f matrix) {
        Vector4f v1 = new Vector4f((float)x, (float)y, 0, 1);
        Vector4f v2 = new Vector4f((float)getX2(), (float)getY2(), 0, 1);
        matrix.transform(v1);
        matrix.transform(v2);
        x = (int)v1.x();
        y = (int)v1.y();
        setX2((int)v2.x());
        setY2((int)v2.y());
        return this;
    }

    public boolean equals(Box2i cmp) { return x == cmp.x && y == cmp.y && w == cmp.w && h == cmp.h; }

    public Box2i scaledCopy(double xFactor, double yFactor, int xOrigin, int yOrigin) {
        return fromCorners((int)((x - xOrigin) * xFactor + xOrigin), (int)((y - yOrigin) * yFactor + yOrigin), (int)((getX2() - xOrigin) * xFactor + xOrigin), (int)((getY2() - yOrigin) * yFactor + yOrigin));
    }

    public Box2i copy() { return new Box2i(x, y, w, h); }

    public int getX1() { return x; }
    public int getY1() { return y; }
    public int getX2() { return x + w; }
    public int getY2() { return y + h; }
    public int getWidth() { return w; }
    public int getHeight() { return h; }

    public IVec2i getTopLeft() { return new IVec2i(x, y); }
    public IVec2i getTopRight() { return new IVec2i(x + w, y); }
    public IVec2i getBottomLeft() { return new IVec2i(x, y + h); }
    public IVec2i getBottomRight() { return new IVec2i(x + w, y + h); }

    public Box2i setX1(int x1) { x = x1; return this; }
    public Box2i setY1(int y1) { y = y1; return this; }
    public Box2i setX2(int x2) { w = x2 - x; return this; }
    public Box2i setY2(int y2) { h = y2 - y; return this; }
    public Box2i setWidth(int width) { w = width; return this; }
    public Box2i setHeight(int height) { h = height; return this; }

    public Box2i move(int dx, int dy) { x += dx; y += dy; return this; }
    public Box2i move(IVec2i vec) { x += vec.x(); y += vec.y(); return this; }
    public Box2i expand(int x, int y) { w += x; h += y; return this; }
    public Box2i scaleWidthAndHeight(int fw, int fh) { w *= fw; h *= fh; return this; }

    public boolean isIn(int tx, int ty) { return tx >= x && tx <= x + w && ty >= y && ty <= y + h; }
    public boolean isIn(IVec2i v) { return isIn(v.x(), v.y()); }

    public Box2i mergeCut(Box2i area) {
        return fromCorners(Math.max(x, area.x), Math.max(y, area.y), Math.min(getX2(), area.getX2()), Math.min(getY2(), area.getY2()));
    }
}
