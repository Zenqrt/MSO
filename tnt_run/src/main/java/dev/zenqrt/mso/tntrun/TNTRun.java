package dev.zenqrt.mso.tntrun;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayer;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;

import java.io.IOException;
import java.net.URISyntaxException;

public final class TNTRun {

    public static void main(String[] args) throws URISyntaxException, IOException {
        // TODO: Make this a builder instead
        MinestomGameServer server = MinestomGameServer.init(
                (uuid, player) -> new TNTRunPlayer(uuid, player, 0),
                gameServer -> new TNTRunGame(gameServer.getInstance(), new TNTRunConfig(gameServer.getConfigJson()))
        );

        server.start(30066);
    }

}
