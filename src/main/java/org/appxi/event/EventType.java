package org.appxi.event;

import java.util.Objects;

public class EventType<T extends Event> {
    public final EventType<? super T> parent;
    public final String typeId;

    public EventType(String typeId) {
        this(null, typeId);
    }

    public EventType(EventType<? super T> parent, String typeId) {
        Objects.requireNonNull(typeId);

        this.parent = parent;
        this.typeId = typeId;
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
