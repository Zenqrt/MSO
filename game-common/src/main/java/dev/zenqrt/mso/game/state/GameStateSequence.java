package dev.zenqrt.mso.game.state;

import java.util.ArrayList;
import java.util.List;

public class GameStateSequence extends GameState {

    private final List<GameState> states;
    private GameStateRunner runner;

    public GameStateSequence() {
        this.states = new ArrayList<>();
    }

    @Override
    protected void onStateStart() {
        runner = new GameStateRunner(states);
        runner.run();
        this.notifyEnd();
    }

    @Override
    protected void onStateEnd() {
        if (getCurrentState().isActive())
            getCurrentState().end();
    }

    public void addState(GameState state) {
        states.add(state);
    }

    public GameState getCurrentState() {
        return runner.getCurrentState();
    }
}
