package dev.zenqrt.mso.parkourrace.game;

import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.game.state.pregame.ConfigureIncomingPlayersGameState;
import dev.zenqrt.mso.game.state.pregame.DisplaySidebarGameState;
import dev.zenqrt.mso.game.state.pregame.MinestomPregameGameState;
import dev.zenqrt.mso.parkourrace.game.player.ParkourRacePlayer;
import dev.zenqrt.mso.parkourrace.game.states.DisableCollisionGameState;
import dev.zenqrt.mso.parkourrace.game.states.DisplayCheckpointsGameState;
import dev.zenqrt.mso.parkourrace.game.states.RaceGameState;
import dev.zenqrt.mso.parkourrace.sidebar.ParkourRaceSidebar;
import map.ParkourRaceConfig;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

public final class ParkourRaceGame extends MinestomGame<ParkourRacePlayer> {

    private final ParkourRaceConfig config;
    private final Map<Player, ParkourRaceSidebar> sidebars = new HashMap<>();

    public ParkourRaceGame(Instance instance, ParkourRaceConfig config) {
        super(instance, new HashMapGamePlayerList<>());

        this.config = config;
    }

    @Override
    protected void populateSequence(GameStateSequence sequence) {
        GameState pregame = MinestomPregameGameState.builder(this)
                .addState(new ConfigureIncomingPlayersGameState(getEventNode(), player -> {
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setRespawnPoint(config.spawnPosition());
                }))
                .addState(new DisplaySidebarGameState<>(getEventNode(), getPlayerList(), sidebars, () -> new ParkourRaceSidebar(config.checkpoints().length)))
                .addState(new DisableCollisionGameState(getEventNode()))
                .addState(new DisplayCheckpointsGameState(getInstance(), config))
                .build();

        sequence.addState(pregame);
        sequence.addState(new RaceGameState(getEventNode(), getPlayerList(), config, getScoreKeeper(), sidebars));
    }
}
