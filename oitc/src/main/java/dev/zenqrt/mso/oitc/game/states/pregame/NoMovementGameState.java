package dev.zenqrt.mso.oitc.game.states.pregame;

import dev.zenqrt.mso.game.state.EventGameState;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;

public final class NoMovementGameState extends EventGameState {

    public NoMovementGameState(EventNode<Event> parentNode) {
        super(parentNode);
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(NoMovementGameState::hasMovedPosition)
                .handler(event -> event.setCancelled(true))
                .build());
    }

    private static boolean hasMovedPosition(PlayerMoveEvent event) {
        Pos oldPos = event.getPlayer().getPosition();
        Pos newPos = event.getNewPosition();
        return oldPos.x() - newPos.x() != 0 || oldPos.y() - newPos.y() != 0 || oldPos.z() - newPos.z() != 0;
    }
}
