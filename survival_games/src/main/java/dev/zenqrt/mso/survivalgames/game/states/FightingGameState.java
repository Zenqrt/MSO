package dev.zenqrt.mso.survivalgames.game.states;

import dev.zenqrt.mso.game.state.EventGameState;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

public final class FightingGameState extends EventGameState {

    public FightingGameState(EventNode<Event> parentNode) {
        super(parentNode);
    }

    @Override
    protected void registerEvents() {

    }
}
