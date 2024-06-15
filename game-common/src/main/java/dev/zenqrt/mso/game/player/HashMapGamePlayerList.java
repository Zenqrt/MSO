package dev.zenqrt.mso.game.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class HashMapGamePlayerList<T extends GamePlayer> implements GamePlayerList<T> {

    private final Map<UUID, T> players = new HashMap<>();

    protected abstract boolean canJoinGame();

    @Override
    public boolean addPlayer(T gamePlayer) {
        UUID uuid = gamePlayer.uuid();

        if (!(canJoinGame() && !players.containsKey(uuid)))
            return false;

        players.put(uuid, gamePlayer);
        return true;
    }

    @Override
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    @Override
    public boolean updatePlayer(T gamePlayer) {
        UUID uuid = gamePlayer.uuid();

        if (!players.containsKey(uuid))
            return false;

        players.replace(uuid, gamePlayer);
        return true;
    }

    @Override
    public boolean hasPlayer(UUID uuid) {
        return players.containsKey(uuid);
    }

    @Override
    public T getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Override
    public Map<UUID, T> getPlayers() {
        return players;
    }
}
