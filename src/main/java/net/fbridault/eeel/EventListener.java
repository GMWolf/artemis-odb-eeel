package net.fbridault.eeel;

public interface EventListener<T> {
    void process(T event, int entity);
}
