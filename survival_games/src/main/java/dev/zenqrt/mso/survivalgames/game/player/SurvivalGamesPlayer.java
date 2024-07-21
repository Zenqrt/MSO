package dev.zenqrt.mso.survivalgames.game.player;

import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public record SurvivalGamesPlayer(UUID uuid, Player player, int score, int kills) implements MinestomGamePlayer {

    @Override
    public SurvivalGamesPlayer withScore(int score) {
        return new SurvivalGamesPlayer(uuid, player, score, kills);
    }
}
