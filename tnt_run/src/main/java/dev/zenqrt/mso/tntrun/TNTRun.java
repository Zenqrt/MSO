package dev.zenqrt.mso.tntrun;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayer;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;

public final class TNTRun {

    public static void main(String[] args) {
        MinestomGameServer.builder(TNTRunPlayer.class)
                .gamePlayerProvider(TNTRunPlayer::new)
                .gameSupplier(server -> new TNTRunGame(server.getInstance(), new TNTRunConfig(server.getConfigJson())))
                .start(30066);
    }

}
