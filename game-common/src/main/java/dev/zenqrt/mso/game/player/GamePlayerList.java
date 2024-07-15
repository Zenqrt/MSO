package dev.zenqrt.mso.game.player;

import net.kyori.adventure.audience.Audience;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface GamePlayerList<T extends GamePlayer> {
    boolean addPlayer(T gamePlayer);
    void removePlayer(UUID uuid);
    boolean updatePlayer(T gamePlayer);
    boolean hasPlayer(UUID uuid);
    T getPlayer(UUID uuid);
    Map<UUID, T> getPlayers();

    default T updatePlayer(UUID uuid, Function<T, T> playerUpdateFunction) {
        T updated = playerUpdateFunction.apply(getPlayer(uuid));
        updatePlayer(updated);
        return updated;
    }

    default Audience getPlayersAsAudience() {
        return Audience.audience(getPlayers().values().stream()
                .map(GamePlayer::player)
                .collect(Collectors.toSet()));
    }

    default void forEach(Consumer<T> handler) {
        getPlayers().forEach((_, gamePlayer) -> handler.accept(gamePlayer));
    }
}
