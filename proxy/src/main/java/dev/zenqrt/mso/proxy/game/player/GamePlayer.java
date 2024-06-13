package dev.zenqrt.mso.proxy.game.player;

import java.util.UUID;

public record GamePlayer(UUID uuid, int score) {

    public GamePlayer withScore(int score) {
        return new GamePlayer(uuid, score);
    }

    public GamePlayer addScore(int score) {
        return withScore(this.score + score);
    }
}
