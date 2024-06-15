package dev.zenqrt.mso.game.player;

import net.kyori.adventure.audience.Audience;

import java.util.UUID;

public interface GamePlayer {
    UUID uuid();
    Audience player();
    int score();

    GamePlayer withScore(int score);
}
