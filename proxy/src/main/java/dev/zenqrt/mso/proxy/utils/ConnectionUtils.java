package dev.zenqrt.mso.proxy.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.zenqrt.mso.proxy.utils.text.Messages;

import java.util.Optional;

public final class ConnectionUtils {

    private ConnectionUtils() {}

    public static void connectTo(RegisteredServer server, Player player) {
        Optional<ServerConnection> serverOptional = player.getCurrentServer();

        if (serverOptional.isPresent() && serverOptional.get().getServer().equals(server)) {
            player.sendMessage(Messages.error("You are already connected to this server!"));
            return;
        }

        player.createConnectionRequest(server)
                .connect()
                .thenAccept(result -> {
                    if (result.isSuccessful())
                        player.sendMessage(Messages.success("Connected to <yellow>" + server.getServerInfo().getName() + "</yellow>."));
                    else
                        player.sendMessage(Messages.error("An error occurred while trying to connect to the server."));
                });
    }

    public static void sendAllPlayersToServer(ProxyServer proxy, RegisteredServer server) {
        proxy.getAllPlayers().forEach(player -> player.createConnectionRequest(server).connect());
    }

}
