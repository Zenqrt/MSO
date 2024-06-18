package dev.zenqrt.mso.tntrun.game;

import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayerList;
import dev.zenqrt.mso.tntrun.game.states.CountdownGameState;
import dev.zenqrt.mso.tntrun.game.states.RunningGameState;
import dev.zenqrt.mso.tntrun.game.states.StatisticShowcaseGameState;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

public final class TNTRunGame extends MinestomGame {

    private final EventNode<Event> eventNode = EventNode.all("tnt_run");
    private final TNTRunPlayerList playerList;

    public TNTRunGame(Instance instance, TNTRunConfig config) {
        super(instance);

        this.playerList = new TNTRunPlayerList(this);

        Map<Integer, GamePlayer> topPlayers = new HashMap<>();

        this.addState(new CountdownGameState(this));
        this.addState(new RunningGameState(eventNode, this, config, topPlayers, getScoreKeeper()));
        this.addState(new StatisticShowcaseGameState(this, topPlayers, getScoreKeeper()));
    }

    @Override
    protected void onLastStateFinished() {
        end();
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();

        MinecraftServer.getGlobalEventHandler().removeChild(eventNode);
    }

    public TNTRunPlayerList getPlayerList() {
        return playerList;
    }
}
