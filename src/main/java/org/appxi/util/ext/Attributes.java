package org.appxi.util.ext;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Attributes {
    private final Map<Object, Object> attributes = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Attributes> T attr(Object key, Object val) {
        this.attributes.put(key, val);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public final <T> T attr(Object key) {
        return (T) this.attributes.get(key);
    }

    public final String attrStr(Object key) {
        final Object val = this.attributes.get(key);
        return null == val ? null : val.toString();
    }

    public final <T> T attrOr(Object key, Supplier<T> defaultValue) {
        final T val = (T) this.attributes.get(key);
        return null != val ? val : defaultValue == null ? null : defaultValue.get();
    }

    @SuppressWarnings("unchecked")
    public final <T> T removeAttr(Object key) {
        return (T) this.attributes.remove(key);
    }

    @SuppressWarnings("unchecked")
    public final <T extends Attributes> T removeAttrs(Predicate<Object> filter) {
        final Set<Object> keys = new HashSet<>(this.attributes.keySet());
        for (Object key : keys) {
            if (null != filter && filter.test(key)) {
                this.attributes.remove(key);
            }
        }
        return (T) this;
    }

    public final boolean hasAttr(Object key) {
        return this.attributes.containsKey(key);
    }

    public final boolean hasAttr(Object key, Object val) {
        return Objects.equals(this.attributes.get(key), val);
    }

    public final Set<Object> attrKeys() {
        return Collections.unmodifiableSet(this.attributes.keySet());
    }
}
