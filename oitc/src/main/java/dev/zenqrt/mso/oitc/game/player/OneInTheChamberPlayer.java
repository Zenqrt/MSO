package dev.zenqrt.mso.oitc.game.player;

import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import net.minestom.server.entity.Player;

import java.util.UUID;

public record OneInTheChamberPlayer(UUID uuid, Player player, int score, int kills) implements MinestomGamePlayer {

    @Override
    public OneInTheChamberPlayer withScore(int score) {
        return new OneInTheChamberPlayer(uuid, player, score, kills);
    }

    public OneInTheChamberPlayer addKill() {
        return new OneInTheChamberPlayer(uuid, player, score, kills + 1);
    }
}
