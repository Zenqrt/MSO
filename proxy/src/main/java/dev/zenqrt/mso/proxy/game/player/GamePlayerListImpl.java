package dev.zenqrt.mso.proxy.game.player;

import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.state.states.IntermissionGameState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GamePlayerListImpl implements GamePlayerList {

    private final MSOGame game;
    private final Map<UUID, GamePlayer> players = new HashMap<>();

    public GamePlayerListImpl(MSOGame game) {
        this.game = game;
    }

    @Override
    public boolean addPlayer(GamePlayer gamePlayer) {
        UUID uuid = gamePlayer.uuid();

        if (!(canJoinGame() && !players.containsKey(uuid)))
            return false;

        players.put(uuid, gamePlayer);
        return true;
    }

    private boolean canJoinGame() {
        return game.getState() instanceof IntermissionGameState;
    }

    @Override
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    @Override
    public boolean updatePlayer(GamePlayer gamePlayer) {
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
    public GamePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    @Override
    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }
}
