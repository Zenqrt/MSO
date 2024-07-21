package dev.zenqrt.mso.oitc.game;

import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.game.state.StatisticShowcaseGameState;
import dev.zenqrt.mso.game.state.pregame.ConfigureIncomingPlayersGameState;
import dev.zenqrt.mso.game.state.pregame.DisplaySidebarGameState;
import dev.zenqrt.mso.game.state.pregame.MinestomPregameGameState;
import dev.zenqrt.mso.oitc.game.map.OneInTheChamberConfig;
import dev.zenqrt.mso.oitc.game.player.OneInTheChamberPlayer;
import dev.zenqrt.mso.oitc.game.states.BattleGameState;
import dev.zenqrt.mso.oitc.game.states.pregame.NoMovementGameState;
import dev.zenqrt.mso.oitc.sidebar.OneInTheChamberSidebar;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class OneInTheChamberGame extends MinestomGame<OneInTheChamberPlayer> {

    private final OneInTheChamberConfig config;
    private final AtomicInteger availableSpawnIndex;
    private final Map<Player, OneInTheChamberSidebar> sidebars = new HashMap<>();

    public OneInTheChamberGame(Instance instance, OneInTheChamberConfig config) {
        super(instance, new HashMapGamePlayerList<>());

        this.config = config;
        this.availableSpawnIndex = new AtomicInteger();
    }

    @Override
    protected void populateSequence(GameStateSequence sequence) {
        GameState pregame = MinestomPregameGameState.builder(this)
                .addState(new ConfigureIncomingPlayersGameState(getEventNode(), player -> {
                    int index = availableSpawnIndex.getAndIncrement();
                    Pos spawnPosition = config.spawnPositions()[index].asPosition();

                    player.setGameMode(GameMode.ADVENTURE);
                    player.setRespawnPoint(spawnPosition);
                }))
                .addState(new DisplaySidebarGameState<>(getEventNode(), getPlayerList(), sidebars,
                        () -> new OneInTheChamberSidebar("Mystical Thing")))
                .addState(new NoMovementGameState(getEventNode()))
                .build();

        sequence.addState(pregame);
        sequence.addState(new BattleGameState(getEventNode(), getInstance(), getPlayerList(), config, getScoreKeeper(), sidebars));
        sequence.addState(new StatisticShowcaseGameState(getPlayerList(), getScoreKeeper()));
    }
}
