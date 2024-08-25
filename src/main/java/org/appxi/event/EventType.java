package org.appxi.event;

import java.util.WeakHashMap;

public class EventType<T extends Event> {
    public final EventType<? super T> parent;
    public final boolean isGlobally;

    WeakHashMap<EventType<? extends T>, Void> children;

    public EventType() {
        this(null, false);
    }

    public EventType(EventType<? super T> parent) {
        this(parent, false);
    }

    public EventType(EventType<? super T> parent, boolean globally) {
        this.parent = parent;
        this.isGlobally = globally;

        if (null != parent) {
            if (null == parent.children) {
                parent.children = new WeakHashMap<>();
            }
            parent.children.put(this, null);
        }
    }

    public Event of() {
        return new Event(this);
    }

    public Event of(Object data) {
        return new Event(this, data);
    }

    public Event.Changed ofChanged(Object from, Object data) {
        return new Event.Changed(this, from, data);
    }

    public Event.Typed ofTyped(String type, Object data) {
        return new Event.Typed(this, type, data);
    }
}
