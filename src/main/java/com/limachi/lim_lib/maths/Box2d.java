package com.limachi.lim_lib.maths;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector4f;
import net.minecraft.world.phys.Vec2;

public class Box2d {
    private double x;
    private double y;
    private double w;
    private double h;

    public Box2d(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        w = width;
        h = height;
    }

    public Box2d(double width, double height) {
        x = 0;
        y = 0;
        w = width;
        h = height;
    }

    public Box2d(Vec2 volume) {
        x = 0;
        y = 0;
        w = volume.x;
        h = volume.y;
    }

    public Box2d(Vec2 position, Vec2 volume) {
        x = position.x;
        y = position.y;
        w = volume.x;
        h = volume.y;
    }

    public Vec2 getOrigins() { return new Vec2((float)x, (float)y); }

    public static Box2d fromCorners(double x1, double y1, double x2, double y2) { return new Box2d(x1, y1,x2 - x1, y2 - y1); }

    public static Box2d fromCorners(Vec2d topLeft, Vec2d bottomRight) { return fromCorners(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y); }

    public Box2d centerOn(double cx, double cy) { return setX1(cx - getWidth() / 2).setY1(cy - getHeight() / 2); }

    public Box2d expandToContain(double px, double py) { return expandToContain(px, py, 0, 0); }

    /**
     * if the given point is outside the box, expand the box so the point fits inside
     * @param px
     * @param py
     * @param xMargin
     * @param yMargin
     * @return
     */

    public Box2d expandToContain(double px, double py, double xMargin, double yMargin) {
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

    public Box2d expandToContain(Box2d other, double xMargin, double yMargin) { return expandToContain(other.x, other.y, xMargin, yMargin).expandToContain(other.getX2(), other.getY2(), xMargin, yMargin); }

    public Box2d transform(Matrix4f matrix) {
        Vector4f v1 = new Vector4f((float)x, (float)y, 0, 1);
        Vector4f v2 = new Vector4f((float)getX2(), (float)getY2(), 0, 1);
        v1.transform(matrix);
        v2.transform(matrix);
        x = v1.x();
        y = v1.y();
        setX2(v2.x());
        setY2(v2.y());
        return this;
    }

    public boolean equals(Box2d cmp) { return x == cmp.x && y == cmp.y && w == cmp.w && h == cmp.h; }

    public Box2d scaledCopy(double xFactor, double yFactor, double xOrigin, double yOrigin) {
        return fromCorners((int)((x - xOrigin) * xFactor + xOrigin), (int)((y - yOrigin) * yFactor + yOrigin), (int)((getX2() - xOrigin) * xFactor + xOrigin), (int)((getY2() - yOrigin) * yFactor + yOrigin));
    }

    public Box2d copy() { return new Box2d(x, y, w, h); }

    public double getX1() { return x; }
    public double getY1() { return y; }
    public double getX2() { return x + w; }
    public double getY2() { return y + h; }
    public double getWidth() { return w; }
    public double getHeight() { return h; }

    public Vec2d getTopLeft() { return new Vec2d(x, y); }
    public Vec2d getTopRight() { return new Vec2d(x + w, y); }
    public Vec2d getBottomLeft() { return new Vec2d(x, y + h); }
    public Vec2d getBottomRight() { return new Vec2d(x + w, y + h); }

    public Box2d setX1(double x1) { x = x1; return this; }
    public Box2d setY1(double y1) { y = y1; return this; }
    public Box2d setX2(double x2) { w = x2 - x; return this; }
    public Box2d setY2(double y2) { h = y2 - y; return this; }
    public Box2d setWidth(double width) { w = width; return this; }
    public Box2d setHeight(double height) { h = height; return this; }

    public Box2d move(double dx, double dy) { x += dx; y += dy; return this; }
    public Box2d expand(double x, double y) { w += x; h += y; return this; }
    public Box2d scaleWidthAndHeight(double fw, double fh) { w *= fw; h *= fh; return this; }

    public boolean isIn(double tx, double ty) { return tx >= x && tx <= x + w && ty >= y && ty <= y + h; }
    public boolean isIn(Vec2d v) { return isIn(v.x, v.y); }

    public Box2d mergeCut(Box2d area) {
        return fromCorners(Math.max(x, area.x), Math.max(y, area.y), Math.min(getX2(), area.getX2()), Math.min(getY2(), area.getY2()));
    }
}
