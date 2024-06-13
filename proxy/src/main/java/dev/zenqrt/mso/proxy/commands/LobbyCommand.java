package dev.zenqrt.mso.proxy.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;

public final class LobbyCommand {

    public static BrigadierCommand createBrigadierCommand(RegisteredServer lobbyServer) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("lobby")
                        .requires(source -> source instanceof Player)
                        .executes(context -> {
                            ConnectionUtils.connectTo(lobbyServer, (Player) context.getSource());
                            return 1;
                        })
        );
    }

}
