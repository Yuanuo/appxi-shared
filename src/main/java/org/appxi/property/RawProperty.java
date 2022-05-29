package org.appxi.property;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class RawProperty<T> {
    private T value;

    private List<BiConsumer<T, T>> listeners = null;

    public RawProperty() {
        this(null);
    }

    public RawProperty(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public RawProperty<T> set(T value) {
        if (value != this.value) {
            T oldValue = this.value;

            this.value = value;

            if (null != this.listeners) {
                for (BiConsumer<T, T> valueListener : this.listeners) {
                    valueListener.accept(oldValue, value);
                }
            }
        }
        //
        return this;
    }

    public RawProperty<T> addListener(BiConsumer<T, T> valueListener) {
        if (null == this.listeners) {
            this.listeners = new ArrayList<>();
        }
        this.listeners.add(valueListener);
        //
        return this;
    }

    public RawProperty<T> removeListener(BiConsumer<T, T> valueListener) {
        if (null != this.listeners) {
            this.listeners.remove(valueListener);
        }
        //
        return this;
    }

    public RawProperty<T> clearListeners() {
        if (null != this.listeners) {
            this.listeners.clear();
        }
        //
        return this;
    }
}
