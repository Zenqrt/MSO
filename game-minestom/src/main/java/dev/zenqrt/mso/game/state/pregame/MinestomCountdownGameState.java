package dev.zenqrt.mso.game.state.pregame;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.tasks.CountdownTask;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public final class MinestomCountdownGameState extends GameState {

    private final GamePlayerList<?> playerList;
    private Task timerTask;

    public MinestomCountdownGameState(GamePlayerList<?> playerList) {
        this.playerList = playerList;
    }

    @Override
    protected void onStateStart() {
        timerTask = MinecraftServer.getSchedulerManager().scheduleTask(new CountdownTask(this, playerList, 15),
                TaskSchedule.immediate(), TaskSchedule.seconds(1));
    }

    @Override
    protected void onStateEnd() {
        if (timerTask != null && timerTask.isAlive())
            timerTask.cancel();
    }
}
