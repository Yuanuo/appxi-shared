package org.appxi.event;

public final class EventSubscriber {
    private final EventBus eventBus;

    private final EventType<? extends Event> eventType;
    private final EventHandler<? super Event> eventHandler;

    EventSubscriber(EventBus eventBus, EventType<? extends Event> eventType, EventHandler<? super Event> eventHandler) {
        this.eventBus = eventBus;
        this.eventType = eventType;
        this.eventHandler = eventHandler;
    }

    /**
     * Stop listening for events.
     */
    public void unsubscribe() {
        eventBus.removeEventHandler(eventType, eventHandler);
    }
}