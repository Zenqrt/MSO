package dev.zenqrt.mso.spleef;

import dev.zenqrt.mso.game.MinestomGameServer;
import dev.zenqrt.mso.spleef.game.SpleefGame;
import dev.zenqrt.mso.spleef.game.commands.GamemodeCommand;
import dev.zenqrt.mso.spleef.game.map.SpleefConfig;
import dev.zenqrt.mso.spleef.game.player.SpleefPlayer;
import io.github.togar2.pvp.MinestomPvP;
import net.minestom.server.MinecraftServer;

public final class Spleef {

    public static void main(String[] args) {
        MinestomGameServer gameServer = MinestomGameServer.builder(SpleefPlayer.class)
                .gameSupplier(server -> new SpleefGame(server.getInstance(), SpleefConfig.fromJson(server.getConfigJson())))
                .gamePlayerProvider(SpleefPlayer::new)
                .build();

        MinecraftServer.getCommandManager().register(new GamemodeCommand());
        MinestomPvP.init();
        gameServer.start(30069);
    }

}
