package dev.zenqrt.mso.proxy.game;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.player.GamePlayer;
import dev.zenqrt.mso.proxy.game.player.GamePlayerList;
import dev.zenqrt.mso.proxy.game.player.GamePlayerListImpl;
import dev.zenqrt.mso.proxy.game.state.GameState;
import dev.zenqrt.mso.proxy.game.state.states.ActiveGameState;
import dev.zenqrt.mso.proxy.game.state.states.IntermissionGameState;
import dev.zenqrt.mso.proxy.leaderboard.Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MSOGame extends GameState {

    private final GamePlayerList playerList;
    private final RegisteredServer lobbyServer;
    private final MSOTournamentGame[] games;
    private final Leaderboard leaderboard;
    private final List<GameState> states;
    private MSOTournamentGame currentGame;
    private int currentGameIndex;
    private int stateIndex;
    private GameState state;

    public MSOGame(MSOProxy plugin, RegisteredServer lobbyServer, MSOTournamentGame[] games) {
        this.playerList = new GamePlayerListImpl(this);
        this.lobbyServer = lobbyServer;
        this.games = games;
        this.leaderboard = new Leaderboard(3, playerList, (gamePlayers, places) -> gamePlayers.stream()
                .sorted(Comparator.comparingInt(GamePlayer::score))
                .limit(places)
                .toArray(GamePlayer[]::new));
        this.currentGame = games[0];

        this.states = new ArrayList<>();

        for (MSOTournamentGame tournamentGame : games) {
            states.add(new IntermissionGameState(plugin, this));
            states.add(new ActiveGameState(plugin, this, tournamentGame));
        }

        this.state = states.get(0);
    }

    @Override
    protected void onStateStart() {
        state.start();
    }

    public GamePlayerList getPlayerList() {
        return playerList;
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public RegisteredServer getLobbyServer() {
        return lobbyServer;
    }

    public void switchToNextState() {
        if (stateIndex >= states.size() - 1) {
            end();
            return;
        }

        if (!state.end())
            return;

        state = states.get(++stateIndex);
        state.start();
    }

    public void switchToNextGame() {
        if (++currentGameIndex >= games.length) {
            end();
            return;
        }

        currentGame = games[currentGameIndex];
    }

    public MSOTournamentGame getCurrentGame() {
        return currentGame;
    }

    public GameState getState() {
        return state;
    }
}
