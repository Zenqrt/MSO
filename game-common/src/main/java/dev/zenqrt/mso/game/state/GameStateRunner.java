package dev.zenqrt.mso.game.state;

import java.util.Collections;
import java.util.List;

public final class GameStateRunner implements Runnable {

    private final List<GameState> states;
    private GameState currentState;
    private int currentStateIndex;

    public GameStateRunner(List<GameState> states) {
        this.states = Collections.unmodifiableList(states);
        this.currentState = states.getFirst();
    }

    public void run() {
        currentState.start();

        while (true) {
            System.out.println("Checking2");
            if (currentState.isReadyToEnd()) {
                System.out.println("Game Runner says we're ready");
                if (!currentState.end()) {
                    System.out.println("well");
                    continue;
                }

                System.out.println("passing");
                if (currentStateIndex + 1 >= states.size())
                    break;

                currentState = states.get(++currentStateIndex);
                currentState.start();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException _) {}
        }
    }

    public GameState getCurrentState() {
        return currentState;
    }
}
