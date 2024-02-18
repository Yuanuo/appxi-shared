package org.appxi.event;

import java.util.Objects;
import java.util.WeakHashMap;

public class EventType<T extends Event> {
    public final EventType<? super T> parent;
    public final String typeId;
    public final boolean isGlobally;

    WeakHashMap<EventType<? extends T>, Void> children;

    public EventType(String typeId) {
        this(null, typeId, false);
    }

    public EventType(EventType<? super T> parent, String typeId) {
        this(parent, typeId, false);
    }

    public EventType(EventType<? super T> parent, String typeId, boolean globally) {
        Objects.requireNonNull(typeId);

        this.parent = parent;
        this.typeId = typeId;
        this.isGlobally = globally;

        if (null != parent) {
            if (null == parent.children) {
                parent.children = new WeakHashMap<>();
            }
            parent.children.put(this, null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return typeId.equals(((EventType<?>) o).typeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeId);
    }
}
