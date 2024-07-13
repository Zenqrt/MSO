package dev.zenqrt.mso.game;

import dev.zenqrt.mso.game.messages.MessageDataFactory;
import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.messenger.Channels;
import dev.zenqrt.mso.messenger.MessageConnectionManager;
import dev.zenqrt.mso.messenger.SingleChannelMessageSender;
import dev.zenqrt.mso.messenger.rabbitmq.RabbitMQMessenger;

import java.util.concurrent.CompletableFuture;

public abstract class Game<T extends GamePlayer> extends GameState {

    private final GameStateSequence sequence;
    private final GamePlayerList<T> playerList;
    private final ScoreKeeper scoreKeeper;
    private final MessageConnectionManager messageConnectionManager;
    private final SingleChannelMessageSender updateChannelSender;
    private final SingleChannelMessageSender gameTransferChannelSender;

    public Game(GamePlayerList<T> playerList) {
        this.playerList = playerList;
        this.scoreKeeper = new ScoreKeeper();
        this.messageConnectionManager = MessageConnectionManager.fromConnectionSettings();
        this.updateChannelSender = messageConnectionManager.registerConnection(RabbitMQMessenger.createSenderWithId(Channels.UPDATE));
        this.gameTransferChannelSender = messageConnectionManager.registerConnection(RabbitMQMessenger.createSenderWithId(Channels.GAME_TRANSFER));
        this.sequence = new GameStateSequence();
    }

    protected abstract void populateSequence(GameStateSequence sequence);

    @Override
    protected void onStateStart() {
        populateSequence(sequence);

        messageConnectionManager.establishConnections();
        CompletableFuture.runAsync(() -> {
            sequence.start();
            this.end();
        });
    }

    @Override
    protected void onStateEnd() {
        this.sequence.end();

        this.updateChannelSender.sendMessage("proxy", MessageDataFactory.gameEndScores(getScoreKeeper()));
        this.gameTransferChannelSender.sendMessage("proxy", new byte[] {});
        this.messageConnectionManager.closeConnections();
    }

    public GamePlayerList<T> getPlayerList() {
        return playerList;
    }

    public ScoreKeeper getScoreKeeper() {
        return scoreKeeper;
    }

}
