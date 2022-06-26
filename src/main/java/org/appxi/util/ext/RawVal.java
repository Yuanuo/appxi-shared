package org.appxi.util.ext;

import java.util.Objects;

public record RawVal<T>(T value, String title) {
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(value, ((RawVal<?>) o).value);
    }

    @Override
    public String toString() {
        return title;
    }

    /**
     * alias to title
     */
    public String key() {
        return title;
    }

    public static <V> RawVal<V> kv(String key, V value) {
        return new RawVal<>(value, key);
    }

    public static <V> RawVal<V> vk(V value, String key) {
        return new RawVal<>(value, key);
    }
}
