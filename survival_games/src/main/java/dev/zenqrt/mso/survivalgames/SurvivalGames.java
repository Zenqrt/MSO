package dev.zenqrt.mso.survivalgames;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.survivalgames.chest.ChestFeature;
import dev.zenqrt.mso.survivalgames.config.SurvivalGamesConfig;
import dev.zenqrt.mso.survivalgames.game.SurvivalGamesGame;
import dev.zenqrt.mso.survivalgames.game.player.SurvivalGamesPlayer;

public final class SurvivalGames {

    public static void main(String[] args) {
        MinestomGameServer gameServer = MinestomGameServer.builder(SurvivalGamesPlayer.class)
                .gameSupplier(server -> new SurvivalGamesGame(server.getInstance(), SurvivalGamesConfig.fromJson(server.getConfigJson())))
                .gamePlayerProvider(((uuid, player, score) -> new SurvivalGamesPlayer(uuid, player, score, 0)))
                .build();

        gameServer.getInstance().eventNode().addChild(ChestFeature.createEventNode());

        gameServer.start(30069);
    }

}
