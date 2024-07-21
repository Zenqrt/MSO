package dev.zenqrt.mso.parkourrace;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.parkourrace.game.ParkourRaceGame;
import dev.zenqrt.mso.parkourrace.game.player.ParkourRacePlayer;
import dev.zenqrt.mso.parkourrace.map.ParkourRaceConfig;

public final class ParkourRace {

    public static void main(String[] args) {
        MinestomGameServer.builder(ParkourRacePlayer.class)
                .gameSupplier(server -> new ParkourRaceGame(server.getInstance(), ParkourRaceConfig.fromJson(server.getConfigJson())))
                .gamePlayerProvider(((uuid, player, score) -> new ParkourRacePlayer(uuid, player, score, 0)))
                .start(30070);

    }

}
