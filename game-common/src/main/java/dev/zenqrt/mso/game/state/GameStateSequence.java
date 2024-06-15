package dev.zenqrt.mso.game.state;

import java.util.ArrayList;
import java.util.List;

public class GameStateSequence extends GameState {

    private final List<GameState> states;
    private GameState currentState;
    private int currentStateIndex;

    public GameStateSequence() {
        this.states = new ArrayList<>();
        this.currentStateIndex = 0;
    }

    protected void onLastStateFinished() {}

    @Override
    protected void onStateStart() {
        currentState = states.getFirst();

        if (states.isEmpty()) {
            end();
            return;
        }

        currentState.start();
    }

    @Override
    protected void onStateEnd() {
        if (currentStateIndex >= states.size())
            return;

        currentState.end();
    }

    public void switchNextState() {
        if (currentStateIndex >= states.size() - 1) {
            onLastStateFinished();
            return;
        }

        if (!currentState.end())
            return;

        currentState = states.get(++currentStateIndex);
        currentState.start();
    }

    public void switchPreviousState() {
        if (!currentState.end())
            return;

        currentState = states.get(--currentStateIndex);
        currentState.start();
    }

    protected void addState(GameState state) {
        states.add(state);
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
