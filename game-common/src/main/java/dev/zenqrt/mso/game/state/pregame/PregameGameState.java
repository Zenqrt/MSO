package dev.zenqrt.mso.game.state.pregame;

import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PregameGameState extends GameStateSequence {

    public static class Builder {

        private final List<GameState> states = new ArrayList<>();
        private final GameState countdownGameState;

        Builder(GameState countdownGameState) {
            this.countdownGameState = countdownGameState;
        }

        public Builder addState(GameState state) {
            states.add(state);
            return this;
        }

        public GameState build() {
            return new PlayerJoinGameStateGroup(countdownGameState, Collections.unmodifiableList(states));
        }

    }
}
