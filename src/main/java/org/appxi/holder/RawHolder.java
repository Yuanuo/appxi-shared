package org.appxi.holder;

public class RawHolder<T> {
    public T value;

    public RawHolder() {
    }

    public RawHolder(final T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
