package dev.zenqrt.mso.tntrun.game.player;

import dev.zenqrt.mso.game.player.GamePlayer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public record TNTRunPlayer(UUID uuid, Player player, int score) implements GamePlayer {

    @Override
    public TNTRunPlayer withScore(int score) {
        return new TNTRunPlayer(uuid, player, score);
    }
}