package dev.zenqrt.mso.game.state.pregame;

import dev.zenqrt.mso.game.state.EventGameState;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;

import java.util.function.Consumer;

public final class ConfigureIncomingPlayersGameState extends EventGameState {

    private final Consumer<Player> playerConsumer;

    public ConfigureIncomingPlayersGameState(EventNode<Event> parentNode, Consumer<Player> playerConsumer) {
        super(parentNode);

        this.playerConsumer = playerConsumer;
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(AsyncPlayerConfigurationEvent.class, event -> playerConsumer.accept(event.getPlayer()));
    }


}
