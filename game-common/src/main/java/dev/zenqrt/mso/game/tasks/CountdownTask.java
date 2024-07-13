package dev.zenqrt.mso.game.tasks;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.text.GameMessages;

public final class CountdownTask implements Runnable {

    private final GameState state;
    private final GamePlayerList<?> playerList;
    private int timeLeft;

    public CountdownTask(GameState state, GamePlayerList<?> playerList, int time) {
        this.state = state;
        this.playerList = playerList;
        this.timeLeft = time;
    }

    @Override
    public void run() {
        playerList.getPlayersAsAudience().sendActionBar(GameMessages.countdown(timeLeft--));

        if (timeLeft <= 0) {
            state.notifyEnd();
        }
    }

}
