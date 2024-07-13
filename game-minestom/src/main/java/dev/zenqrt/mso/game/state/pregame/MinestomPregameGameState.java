package dev.zenqrt.mso.game.state.pregame;

import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.state.GameState;
import net.minestom.server.entity.Player;

import java.util.function.Consumer;

public final class MinestomPregameGameState extends PregameGameState {

    public static Builder builder(MinestomGame<?> game) {
        return new Builder(new MinestomCountdownGameState(game.getPlayerList()));
    }

    public static GameState createPregame(MinestomGame<?> game, Consumer<Player> playerConfigureHandler) {
          return builder(game)
                  .addState(new ConfigureIncomingPlayersGameState(game.getEventNode(), game.getInstance(), playerConfigureHandler))
                  .build();
    }

}
