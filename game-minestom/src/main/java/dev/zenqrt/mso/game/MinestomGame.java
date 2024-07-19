package dev.zenqrt.mso.game;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.CompletableFuture;

public abstract class MinestomGame<T extends MinestomGamePlayer> extends Game<T> {

    private final EventNode<Event> eventNode = EventNode.all("minestom-game");
    private final EventNode<Event> parentNode;
    private final Instance instance;

    public MinestomGame(Instance instance, GamePlayerList<T> playerList) {
        super(playerList);

        this.parentNode = MinecraftServer.getGlobalEventHandler();
        this.instance = instance;
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        parentNode.addChild(eventNode);
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();

        parentNode.removeChild(eventNode);

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (!MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty())
                return;

            CompletableFuture.runAsync(MinecraftServer::stopCleanly);
        }, TaskSchedule.seconds(1), TaskSchedule.seconds(3));
    }

    public EventNode<Event> getEventNode() {
        return eventNode;
    }

    public Instance getInstance() {
        return instance;
    }
}
