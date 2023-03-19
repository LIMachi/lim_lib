package com.limachi.lim_lib.maths;

@SuppressWarnings("unused")
public record IVec2d(double x, double y) {

    public boolean equals(IVec2d v) {
        return x == v.x && y == v.y;
    }

    public IVec2d copy() {
        return new IVec2d(x, y);
    }

    public IVec2d add(IVec2d other) { return new IVec2d(x + other.x, y + other.y); }
}
