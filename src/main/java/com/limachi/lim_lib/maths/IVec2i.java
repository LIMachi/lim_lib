package com.limachi.lim_lib.maths;

import java.util.Objects;

public record IVec2i(int x, int y) {

    public boolean equals(IVec2i v) { return x == v.x && y == v.y; }

    public IVec2i copy() { return new IVec2i(x, y); }

    public IVec2i add(IVec2i other) { return new IVec2i(x + other.x, y + other.y); }

    public IVec2i sub(IVec2i other) { return new IVec2i(x - other.x, y - other.y); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IVec2i iVec2i = (IVec2i) o;
        return x == iVec2i.x && y == iVec2i.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}