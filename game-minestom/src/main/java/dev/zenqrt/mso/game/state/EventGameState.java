package dev.zenqrt.mso.game.state;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

import java.util.UUID;

public abstract class EventGameState extends GameState {

    protected final EventNode<Event> eventNode;
    private final EventNode<Event> parentNode;
    private boolean hasRegisteredEvents;

    public EventGameState(EventNode<Event> parentNode) {
        this.parentNode = parentNode;
        this.eventNode = EventNode.all(UUID.randomUUID().toString());
    }

    protected abstract void registerEvents();

    @Override
    protected void onStateStart() {
        if (!hasRegisteredEvents) {
            registerEvents();
            hasRegisteredEvents = true;
        }

        parentNode.addChild(this.eventNode);
    }

    @Override
    protected void onStateEnd() {
        parentNode.removeChild(this.eventNode);
    }
}
