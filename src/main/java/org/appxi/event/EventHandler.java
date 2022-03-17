package org.appxi.event;

@FunctionalInterface
public interface EventHandler<T extends Event> {
    void onEvent(T event);
}
