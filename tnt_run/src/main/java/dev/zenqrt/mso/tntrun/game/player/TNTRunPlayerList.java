package dev.zenqrt.mso.tntrun.game.player;

import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.states.CountdownGameState;

public final class TNTRunPlayerList extends HashMapGamePlayerList<TNTRunPlayer> {

    private final TNTRunGame game;

    public TNTRunPlayerList(TNTRunGame game) {
        this.game = game;
    }

    @Override
    protected boolean canJoinGame() {
        return game.getCurrentState() instanceof CountdownGameState;
    }
}
