package dev.zenqrt.mso.tntrun;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayer;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerSpawnEvent;

import java.io.IOException;
import java.net.URISyntaxException;

public final class TNTRun {

    public static void main(String[] args) throws URISyntaxException, IOException {
        MinestomGameServer server = MinestomGameServer.init();

        TNTRunGame game = new TNTRunGame(server.getInstance(), new TNTRunConfig(server.getConfigJson()));
        game.start();

        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event ->
                game.getPlayerList().addPlayer(new TNTRunPlayer(event.getPlayer().getUuid(), event.getPlayer(), 0)));

        server.start(25566);
    }

}
