package com.limachi.lim_lib.maths;

@SuppressWarnings("unused")
public class Vec2d {
    public double x;
    public double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Vec2d v) { return x == v.x && y == v.y; }

    public Vec2d copy() { return new Vec2d(x, y); }
}
