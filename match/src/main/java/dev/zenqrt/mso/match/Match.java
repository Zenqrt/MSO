package dev.zenqrt.mso.match;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.match.game.MatchGame;

import java.io.IOException;
import java.net.URISyntaxException;

public final class Match {

    public static void main(String[] args) throws URISyntaxException, IOException {
        MinestomGameServer server = MinestomGameServer.init();

        MatchGame game = new MatchGame(server.getInstance());
        game.start();

        server.start(30067);
    }

}
