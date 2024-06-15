package dev.zenqrt.mso.tntrun.game.states;

import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.text.GameMessages;
import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayerList;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public final class CountdownGameState extends GameState {

    private final TNTRunGame game;
    private Task timerTask;

    public CountdownGameState(TNTRunGame game) {
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        timerTask = MinecraftServer.getSchedulerManager().scheduleTask(new TimerTask(game, game.getPlayerList(), 15),
                TaskSchedule.immediate(), TaskSchedule.seconds(1));
    }

    @Override
    protected void onStateEnd() {
        timerTask.cancel();
    }

    private static class TimerTask implements Runnable {

        private final TNTRunGame game;
        private final TNTRunPlayerList playerList;
        private int timeLeft;

        TimerTask(TNTRunGame game, TNTRunPlayerList playerList, int time) {
            this.game = game;
            this.playerList = playerList;
            this.timeLeft = time;
        }

        @Override
        public void run() {
            playerList.getPlayersAsAudience().sendActionBar(GameMessages.countdown(timeLeft--));

            if (timeLeft <= 0) {
                game.switchNextState();
            }
        }
    }

}
