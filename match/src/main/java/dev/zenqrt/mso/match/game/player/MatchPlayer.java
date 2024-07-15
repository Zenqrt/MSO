package dev.zenqrt.mso.match.game.player;

import dev.zenqrt.mso.game.player.GamePlayer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public record MatchPlayer(UUID uuid, Player player, int score, int buildsCompleted) implements GamePlayer {

    @Override
    public MatchPlayer withScore(int score) {
        return new MatchPlayer(uuid, player, score, buildsCompleted);
    }

    public MatchPlayer addBuildsCompleted() {
        return new MatchPlayer(uuid, player, score, buildsCompleted + 1);
    }
}
