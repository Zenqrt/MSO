package dev.zenqrt.mso.game;

import dev.zenqrt.mso.messenger.MessageConnectionManager;
import dev.zenqrt.mso.game.messages.PluginMessageFactory;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.messenger.Channels;
import dev.zenqrt.mso.messenger.SingleChannelMessageSender;
import dev.zenqrt.mso.messenger.rabbitmq.RabbitMQMessageSender;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

import java.util.concurrent.CompletableFuture;

public class MinestomGame extends GameStateSequence {

    private final Instance instance;
    private final MessageConnectionManager messageConnectionManager;
    private final SingleChannelMessageSender updateChannelSender;
    private final SingleChannelMessageSender gameTransferChannelSender;
    private final ScoreKeeper scoreKeeper;

    public MinestomGame(Instance instance) {
        this.instance = instance;
        this.messageConnectionManager = MessageConnectionManager.fromConnectionSettings();
        this.updateChannelSender = messageConnectionManager.registerConnection(serverId -> new RabbitMQMessageSender(serverId, Channels.UPDATE));
        this.gameTransferChannelSender = messageConnectionManager.registerConnection(serverId -> new RabbitMQMessageSender(serverId, Channels.GAME_TRANSFER));
        this.scoreKeeper =  new ScoreKeeper();
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        messageConnectionManager.establishConnections();
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();

        this.updateChannelSender.sendMessage("proxy", PluginMessageFactory.gameEndScores(scoreKeeper));
        this.gameTransferChannelSender.sendMessage("proxy", new byte[] {});
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (!MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty())
                return;

            CompletableFuture.runAsync(() -> {
                messageConnectionManager.closeConnections();
                MinecraftServer.stopCleanly();
            });
        }, TaskSchedule.seconds(1), TaskSchedule.seconds(3));
    }

    public Instance getInstance() {
        return instance;
    }

    protected ScoreKeeper getScoreKeeper() {
        return scoreKeeper;
    }
}
