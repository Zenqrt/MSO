package dev.zenqrt.mso.game.player;

import net.kyori.adventure.audience.Audience;

import java.util.UUID;

public interface GamePlayerProvider<T extends GamePlayer, P extends Audience> {
    T createPlayer(UUID uuid, P player, int score);
}
