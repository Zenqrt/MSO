package dev.zenqrt.mso.game.state;

import dev.zenqrt.mso.player.Players;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.EntityEvent;

import java.util.UUID;

public abstract class EventGameState extends GameState {

    protected final EventNode<EntityEvent> eventNode;
    private final EventNode<Event> parentNode;
    private boolean hasRegisteredEvents;

    public EventGameState(EventNode<Event> parentNode) {
        this.parentNode = parentNode;
        this.eventNode = EventNode.type(UUID.randomUUID().toString(), EventFilter.ENTITY,
                (_, entity) -> entity instanceof Player player && !Players.isExcluded(player.getUsername()));
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
