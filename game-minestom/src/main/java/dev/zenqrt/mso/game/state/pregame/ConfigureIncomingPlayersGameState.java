package dev.zenqrt.mso.game.state.pregame;

import dev.zenqrt.mso.game.state.EventGameState;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;

import java.util.function.Consumer;

public final class ConfigureIncomingPlayersGameState extends EventGameState {

    private final Instance instance;
    private final Consumer<Player> playerConsumer;

    public ConfigureIncomingPlayersGameState(EventNode<Event> parentNode, Instance instance, Consumer<Player> playerConsumer) {
        super(parentNode);

        this.instance = instance;
        this.playerConsumer = playerConsumer;
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);
            playerConsumer.accept(event.getPlayer());
        });
    }


}
