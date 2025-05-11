package com.limachi.lim_lib.common.modCreation;

public enum Loaders {
    Forge(1),
    NeoForge(2),
    ForgeLike(3),
    Fabric(4),
    Quilt(8),
    FabricLike(12),
    Any(15);

    private final int mask;

    Loaders(int mask) { this.mask = mask; }

    public boolean matches(Loaders other) { return (mask & other.mask) != 0; }
}
