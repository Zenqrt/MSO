package dev.zenqrt.mso.game;

import dev.zenqrt.mso.game.messages.PluginMessageFactory;
import dev.zenqrt.mso.game.messages.PluginMessenger;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.messenger.ChannelIdentifiers;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

public class MinestomGame extends GameStateSequence {

    private final Instance instance;
    private final ScoreKeeper scoreKeeper;

    public MinestomGame(Instance instance) {
        this.instance = instance;
        this.scoreKeeper =  new ScoreKeeper();
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();

        PluginMessenger.sendPluginMessage(ChannelIdentifiers.UPDATE, PluginMessageFactory.gameEndScores(scoreKeeper));
        PluginMessenger.sendPluginMessage(ChannelIdentifiers.GAME_TRANSFER, "next_state");
        System.out.println("Sent");
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (!MinecraftServer.getConnectionManager().getOnlinePlayers().isEmpty())
                return;

            MinecraftServer.stopCleanly();
        }, TaskSchedule.seconds(1), TaskSchedule.seconds(3));
    }

    public Instance getInstance() {
        return instance;
    }

    protected ScoreKeeper getScoreKeeper() {
        return scoreKeeper;
    }
}
