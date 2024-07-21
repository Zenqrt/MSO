package dev.zenqrt.mso.game.state.pregame;

import dev.zenqrt.mso.game.state.GameState;

import java.util.List;

public class GameStateGroup extends GameState {

    private final List<GameState> states;
    private final GameState conditionalState;

    public GameStateGroup(GameState conditionalState, List<GameState> unmodifiableStates) {
        this.conditionalState = conditionalState;
        this.states = unmodifiableStates;
    }

    @Override
    protected void onStateStart() {
        conditionalState.start();
        states.forEach(GameState::start);
    }

    @Override
    protected void onStateEnd() {
        states.forEach(GameState::end);
        conditionalState.end();
    }

    @Override
    public boolean isReadyToEnd() {
        return conditionalState.isReadyToEnd();
    }
}
