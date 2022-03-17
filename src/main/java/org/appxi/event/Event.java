package org.appxi.event;

import java.util.Objects;

public class Event {
    public static final EventType<?> ANY = new EventType<>("ANY");

    public final EventType<?> eventType;
    public final Object data;

    private boolean consumed;

    public Event(EventType<?> eventType) {
        this(eventType, null);
    }

    public Event(EventType<?> eventType, Object data) {
        Objects.requireNonNull(eventType);

        this.eventType = eventType;
        this.data = data;
    }

    public final <T> T data() {
        //noinspection unchecked
        return (T) data;
    }

    public final void setConsumed() {
        this.consumed = true;
    }

    public final boolean isConsumed() {
        return consumed;
    }
}
