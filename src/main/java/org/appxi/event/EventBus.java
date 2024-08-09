package org.appxi.event;

/**
 * An event dispatcher that can be used for subscribing to events and posting
 * the events.
 */
public final class EventBus {
    private static final EventBus global = new EventBus();

    private final EventHandlers eventHandlers = new EventHandlers();

    /**
     * Register event handler for event type.
     *
     * @param eventType    type
     * @param eventHandler handler
     * @param <T>          event
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> EventSubscriber addEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        final EventBus eventBus = eventType.isGlobally ? global : this;
        eventBus.eventHandlers.addEventHandler(eventType, eventHandler);
        return new EventSubscriber(eventBus, eventType, (EventHandler<? super Event>) eventHandler);
    }

    public <T extends Event> void addEventsHandler(EventType<T>[] eventTypes, EventHandler<? super T> eventHandler) {
        for (EventType<T> eventType: eventTypes) {
            final EventBus eventBus = eventType.isGlobally ? global : this;
            eventBus.eventHandlers.addEventHandler(eventType, eventHandler);
            new EventSubscriber(eventBus, eventType, (EventHandler<? super Event>) eventHandler);
        }
    }

    /**
     * Remove event handler for event type.
     *
     * @param eventType    type
     * @param eventHandler handler
     * @param <T>          event
     */
    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<? super T> eventHandler) {
        final EventBus eventBus = eventType.isGlobally ? global : this;
        eventBus.eventHandlers.removeEventHandler(eventType, eventHandler);
    }

    /**
     * Post (fire) given event. All listening parties will be notified. Events will
     * be handled on the same thread that fired the event, i.e. synchronous.
     *
     * <p>
     * Note: according to JavaFX doc this must be called on JavaFX DesktopApplication
     * Thread. In reality this doesn't seem to be true.
     * </p>
     *
     * @param event the event
     */
    public void fireEvent(Event event) {
        final EventBus eventBus = event.eventType.isGlobally ? global : this;
        eventBus.eventHandlers.fireEvent(event);
    }
}