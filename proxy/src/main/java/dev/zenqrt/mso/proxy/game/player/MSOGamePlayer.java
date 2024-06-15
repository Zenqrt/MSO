package dev.zenqrt.mso.proxy.game.player;

import com.velocitypowered.api.proxy.Player;
import dev.zenqrt.mso.game.player.GamePlayer;

import java.util.UUID;

public record MSOGamePlayer(UUID uuid, Player player, int score) implements GamePlayer {

    public MSOGamePlayer withScore(int score) {
        return new MSOGamePlayer(uuid, player, score);
    }

    public MSOGamePlayer addScore(int score) {
        return withScore(this.score + score);
    }
}
