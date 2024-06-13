package dev.zenqrt.mso.proxy.game.player;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface GamePlayerList {
    boolean addPlayer(GamePlayer gamePlayer);
    void removePlayer(UUID uuid);
    boolean updatePlayer(GamePlayer gamePlayer);
    boolean hasPlayer(UUID uuid);
    GamePlayer getPlayer(UUID uuid);
    Map<UUID, GamePlayer> getPlayers();

    default void forEach(Consumer<GamePlayer> handler) {
        getPlayers().forEach((ignored, gamePlayer) -> handler.accept(gamePlayer));
    }
}
