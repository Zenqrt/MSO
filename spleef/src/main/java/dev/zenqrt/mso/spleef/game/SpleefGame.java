package dev.zenqrt.mso.spleef.game;

import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.game.state.StatisticShowcaseGameState;
import dev.zenqrt.mso.game.state.pregame.ConfigureIncomingPlayersGameState;
import dev.zenqrt.mso.game.state.pregame.MinestomPregameGameState;
import dev.zenqrt.mso.spleef.game.map.SpleefConfig;
import dev.zenqrt.mso.spleef.game.player.SpleefPlayer;
import dev.zenqrt.mso.spleef.game.state.SpleefGameState;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.Instance;

public final class SpleefGame extends MinestomGame<SpleefPlayer> {

    private final SpleefConfig config;

    public SpleefGame(Instance instance, SpleefConfig config) {
        super(instance, new HashMapGamePlayerList<>());

        this.config = config;
    }

    @Override
    protected void populateSequence(GameStateSequence sequence) {
        GameState pregame = MinestomPregameGameState.builder(this)
                .addState(new ConfigureIncomingPlayersGameState(getEventNode(), player -> {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setRespawnPoint(config.spawn());
                }))
                .build();

        sequence.addState(pregame);
        sequence.addState(new SpleefGameState(getEventNode(), getInstance(), getPlayerList(), config, getScoreKeeper()));
        sequence.addState(new StatisticShowcaseGameState(getPlayerList(), getScoreKeeper()));
    }
}
