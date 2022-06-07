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
        // 注册给定事件类型
        getEventHandlers(eventType).add(eventHandler);

        // 同时注册给定事件类型的所有（多级）子类型
        // 此处必须注册以保证fireEvent时能获取到本类型及子类型
        if (null != eventType.children) {
            eventType.children.forEach((k, v) -> addEventHandler(k, eventHandler));
        }
    }

    void removeEventHandler(EventType<?> eventType, EventHandler<?> eventHandler) {
        // 移除给定事件类型
        getEventHandlers(eventType).remove(eventHandler);

        // 同时移除给定事件类型的所有（多级）子类型
        if (null != eventType.children) {
            eventType.children.forEach((k, v) -> removeEventHandler(k, eventHandler));
        }
    }

    void fireEvent(Event event) {
        // 在注册事件类型时已同时注册了所有子类型，此时给定event的事件类型获得的所有绑定函数是正确的
        for (EventHandler eventHandler : new ArrayList<>(getEventHandlers(event.eventType))) {
            eventHandler.handle(event);
            if (event.isConsumed()) {
                return;
            }
        }
    }
}
