package org.appxi.event;

import java.util.Objects;

public class Event {
    public static final EventType<Event> ANY = new EventType<>("ANY");

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

    public final void consume() {
        this.consumed = true;
    }

    public final boolean isConsumed() {
        return consumed;
    }

    public final EventType<?> getEventType() {
        return eventType;
    }

    public static class Changed extends Event{
        public final Object from;

        public Changed(EventType<?> eventType, Object from, Object data) {
            super(eventType, data);
            this.from = from;
        }
    }

    public static class Typed extends Event{
        public final String type;

        public Typed(EventType<?> eventType, String type, Object data) {
            super(eventType, data);
            this.type = type;
        }
    }
}
