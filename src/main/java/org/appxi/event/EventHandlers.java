package org.appxi.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EventHandlers {
    private final Map<EventType<?>, List<EventHandler<?>>> eventHandlers = new HashMap<>();

    List<EventHandler<?>> getEventHandlers(EventType<?> eventType) {
        return eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>());
    }

    void addEventHandler(EventType<?> eventType, EventHandler<?> eventHandler) {
        while (null != eventType) {
            getEventHandlers(eventType).add(eventHandler);

            eventType = eventType.parent;
        }
    }

    void removeEventHandler(EventType<?> eventType, EventHandler<?> eventHandler) {
        while (null != eventType) {
            getEventHandlers(eventType).remove(eventHandler);
            eventType = eventType.parent;
        }
    }

    void fireEvent(Event event) {
        for (EventHandler eventHandler : new ArrayList<>(getEventHandlers(event.eventType))) {
            eventHandler.onEvent(event);
            if (event.isConsumed()) break;
        }
    }
}
