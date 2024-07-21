package dev.zenqrt.mso.tntrun.game;

import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.game.state.StatisticShowcaseGameState;
import dev.zenqrt.mso.game.state.pregame.MinestomPregameGameState;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayer;
import dev.zenqrt.mso.tntrun.game.states.FloorFallGameState;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.Instance;

public final class TNTRunGame extends MinestomGame<TNTRunPlayer> {

    private final TNTRunConfig config;

    public TNTRunGame(Instance instance, TNTRunConfig config) {
        super(instance, new HashMapGamePlayerList<>());

        this.config = config;
    }

    @Override
    protected void populateSequence(GameStateSequence sequence) {
        GameState pregame = MinestomPregameGameState.createPregame(this, player -> {
            player.setGameMode(GameMode.ADVENTURE);
            player.setRespawnPoint(config.spawnPosition());
        });
        sequence.addState(pregame);
        sequence.addState(new FloorFallGameState(getEventNode(), this, config));
        sequence.addState(new StatisticShowcaseGameState(getPlayerList(), getScoreKeeper()));
    }
}
