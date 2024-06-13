package dev.zenqrt.mso.game.player;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface GamePlayerList<T extends GamePlayer> {
    boolean addPlayer(T gamePlayer);
    void removePlayer(UUID uuid);
    boolean updatePlayer(T gamePlayer);
    boolean hasPlayer(UUID uuid);
    T getPlayer(UUID uuid);
    Map<UUID, T> getPlayers();

    default void forEach(Consumer<T> handler) {
        getPlayers().forEach((_, gamePlayer) -> handler.accept(gamePlayer));
    }
}
