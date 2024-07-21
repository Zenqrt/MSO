package dev.zenqrt.mso.spleef.game.player;

import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public record SpleefPlayer(UUID uuid, Player player, int score) implements MinestomGamePlayer {
    @Override
    public SpleefPlayer withScore(int score) {
        return new SpleefPlayer(uuid, player, score);
    }
}
