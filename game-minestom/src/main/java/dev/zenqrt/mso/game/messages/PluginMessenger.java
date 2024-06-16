package dev.zenqrt.mso.game.messages;

import net.minestom.server.MinecraftServer;

import java.nio.charset.StandardCharsets;

public final class PluginMessenger {

    private PluginMessenger() {}

    public static void sendPluginMessage(String channel, byte[] data) {
        MinecraftServer.getConnectionManager().getOnlinePlayers().stream()
                .findFirst()
                .ifPresentOrElse(player -> player.sendPluginMessage(channel, data),
                        () -> { throw new RuntimeException("Unable to send plugin message while no players are online"); });
    }

    public static void sendPluginMessage(String channel, String message) {
        sendPluginMessage(channel, message.getBytes(StandardCharsets.UTF_8));
    }

}
