package com.limachi.lim_lib.data;

import com.limachi.lim_lib.Log;

/**
 * Interface to create an object that should be set (to non-null) exactly once.
 * Trying to set the value while it has already been set will result in an error.
 * Trying to access the value while it has not been set will also result in an error.
 * @param <T> type of the object to be handled
 */
public final class FunctionallyFinal<T> {
    private T object = null;
    private boolean set = false;

    public boolean set(T object) {
        if (!set) {
            this.object = object;
            set = true;
            return true;
        }
        Log.error(this, 1, "Tried to overwrite set value!");
        return false;
    }

    public T get() {
        if (!set)
            Log.error(this, 1, "Tried to access unset value!");
        return object;
    }

    public boolean isSet() { return set; }
}
