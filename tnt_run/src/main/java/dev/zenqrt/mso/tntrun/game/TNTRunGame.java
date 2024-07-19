package dev.zenqrt.mso.tntrun.game;

import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.game.state.pregame.MinestomPregameGameState;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayer;
import dev.zenqrt.mso.tntrun.game.states.RunningGameState;
import dev.zenqrt.mso.tntrun.game.states.StatisticShowcaseGameState;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

public final class TNTRunGame extends MinestomGame<TNTRunPlayer> {

    private final TNTRunConfig config;

    public TNTRunGame(Instance instance, TNTRunConfig config) {
        super(instance, new HashMapGamePlayerList<>());

        this.config = config;
    }

    @Override
    protected void populateSequence(GameStateSequence sequence) {
        Map<Integer, GamePlayer> topPlayers = new HashMap<>();

        GameState pregame = MinestomPregameGameState.createPregame(this, player -> {
            player.setGameMode(GameMode.ADVENTURE);
            player.setRespawnPoint(config.spawnPosition());
        });
        sequence.addState(pregame);

        sequence.addState(new RunningGameState(getEventNode(), this, config, topPlayers));
        sequence.addState(new StatisticShowcaseGameState(this, topPlayers));
    }
}
