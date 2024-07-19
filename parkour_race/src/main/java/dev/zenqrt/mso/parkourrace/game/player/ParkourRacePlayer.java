package dev.zenqrt.mso.parkourrace.game.player;

import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public record ParkourRacePlayer(UUID uuid, Player player, int score, int checkpointNumber) implements MinestomGamePlayer {

    public ParkourRacePlayer addCheckpointNumber() {
        return withCheckpointNumber(checkpointNumber + 1);
    }

    public ParkourRacePlayer withCheckpointNumber(int checkpointNumber) {
        return new ParkourRacePlayer(uuid, player, score, checkpointNumber);
    }

    @Override
    public ParkourRacePlayer withScore(int score) {
        return new ParkourRacePlayer(uuid, player, score, checkpointNumber);
    }
}
