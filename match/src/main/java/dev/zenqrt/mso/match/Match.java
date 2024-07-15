package dev.zenqrt.mso.match;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.match.game.MatchGame;
import dev.zenqrt.mso.match.game.map.MatchConfig;
import dev.zenqrt.mso.match.game.player.MatchPlayer;

public final class Match {

    public static void main(String[] args) {
        MinestomGameServer.builder(MatchPlayer.class)
                .gamePlayerProvider(((uuid, player, score) -> new MatchPlayer(uuid, player, score, 0)))
                .gameSupplier(server -> new MatchGame(server.getInstance(), MatchConfig.fromJson(server.getConfigJson())))
                .start(30068);
    }

}
