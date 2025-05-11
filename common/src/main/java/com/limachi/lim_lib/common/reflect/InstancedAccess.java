package com.limachi.lim_lib.common.reflect;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface InstancedAccess<I, T> extends Access<T>, Function<I, T>, BiConsumer<I, T> {}