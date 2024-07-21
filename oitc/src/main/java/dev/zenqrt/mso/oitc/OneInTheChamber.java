package dev.zenqrt.mso.oitc;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.oitc.game.OneInTheChamberGame;
import dev.zenqrt.mso.oitc.game.map.OneInTheChamberConfig;
import dev.zenqrt.mso.oitc.game.player.OneInTheChamberPlayer;
import io.github.togar2.pvp.MinestomPvP;

public final class OneInTheChamber {

    public static void main(String[] args) {
        MinestomGameServer gameServer = MinestomGameServer.builder(OneInTheChamberPlayer.class)
                .gameSupplier(server -> new OneInTheChamberGame(server.getInstance(), OneInTheChamberConfig.fromJson(server.getConfigJson())))
                .gamePlayerProvider((uuid, player, score) -> new OneInTheChamberPlayer(uuid, player, score, 0))
                .build();

        MinestomPvP.init();
        gameServer.getInstance().setTime(13000);
        gameServer.start(30067);
    }

}
