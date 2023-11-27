package org.appxi.util.ext;

import java.util.Objects;

public record RawVal2<K, V>(K key, V value) {
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(value, ((RawVal2<?, ?>) o).value);
    }

    @Override
    public String toString() {
        return String.valueOf(key);
    }

    public static <K, V> RawVal2<K, V> kv(K key, V value) {
        return new RawVal2<>(key, value);
    }

    public static <K, V> RawVal2<K, V> vk(V value, K key) {
        return new RawVal2<>(key, value);
    }
}
