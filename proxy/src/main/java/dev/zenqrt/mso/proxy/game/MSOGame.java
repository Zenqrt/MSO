package dev.zenqrt.mso.proxy.game;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.messenger.Channels;
import dev.zenqrt.mso.messenger.MessageConnectionManager;
import dev.zenqrt.mso.messenger.SingleChannelMessageReceiver;
import dev.zenqrt.mso.messenger.SingleChannelMessageSender;
import dev.zenqrt.mso.messenger.rabbitmq.RabbitMQMessenger;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayer;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayerList;
import dev.zenqrt.mso.proxy.game.state.states.ActiveGameState;
import dev.zenqrt.mso.proxy.game.state.states.IntermissionGameState;
import dev.zenqrt.mso.proxy.leaderboard.Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MSOGame extends GameState {

    private final MSOGamePlayerList playerList;
    private final RegisteredServer lobbyServer;
    private final MessageConnectionManager messageConnectionManager;
    private final SingleChannelMessageSender infoChannelSender;
    private final SingleChannelMessageReceiver gameTransferChannelReceiver;
    private final SingleChannelMessageReceiver updateChannelReceiver;
    private final MSOTournamentGame[] games;
    private final Leaderboard leaderboard;
    private final List<GameState> states;
    private MSOTournamentGame currentGame;
    private int currentGameIndex;
    private int stateIndex;
    private GameState state;

    public MSOGame(MSOProxy plugin, RegisteredServer lobbyServer, MSOTournamentGame[] games) {
        this.playerList = new MSOGamePlayerList(this);
        this.lobbyServer = lobbyServer;

        this.messageConnectionManager = MessageConnectionManager.fromConnectionSettings();
        this.infoChannelSender = messageConnectionManager.registerConnection(RabbitMQMessenger.createSenderWithId(Channels.INFO));
        this.gameTransferChannelReceiver = messageConnectionManager.registerConnection(RabbitMQMessenger.createReceiverWithId(Channels.GAME_TRANSFER));
        this.updateChannelReceiver = messageConnectionManager.registerConnection(RabbitMQMessenger.createReceiverWithId(Channels.UPDATE));

        this.games = games;
        this.leaderboard = new Leaderboard(3, playerList, (gamePlayers, places) -> gamePlayers.stream()
                .sorted(Comparator.comparingInt(MSOGamePlayer::score))
                .limit(places)
                .toArray(MSOGamePlayer[]::new));
        this.currentGame = games[0];

        this.states = new ArrayList<>();

        for (MSOTournamentGame tournamentGame : games) {
            states.add(new IntermissionGameState(plugin, this, 300));
            states.add(new ActiveGameState(plugin, this, tournamentGame));
        }

        this.state = states.getFirst();
    }

    @Override
    protected void onStateStart() {
        messageConnectionManager.establishConnections();
        state.start();
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();
        state.end();
        messageConnectionManager.closeConnections();
    }

    public MSOGamePlayerList getPlayerList() {
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

    public MSOTournamentGame setCurrentGame(int index) {
        currentGameIndex = index;
        return currentGame = games[index];
    }

    public MSOTournamentGame getCurrentGame() {
        return currentGame;
    }

    public GameState getState() {
        return state;
    }

    public SingleChannelMessageSender getInfoChannelSender() {
        return infoChannelSender;
    }

    public SingleChannelMessageReceiver getGameTransferChannelReceiver() {
        return gameTransferChannelReceiver;
    }

    public SingleChannelMessageReceiver getUpdateChannelReceiver() {
        return updateChannelReceiver;
    }
}
