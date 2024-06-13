package dev.zenqrt.mso.tntrun.game.state.states;

import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.state.GameState;

public final class WaitingGameState extends GameState {

    private final TNTRunGame game;

    public WaitingGameState(TNTRunGame game) {
        this.game = game;
    }

}
