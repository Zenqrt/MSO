package dev.zenqrt.mso.proxy.utils.messages;

import com.velocitypowered.api.proxy.ProxyServer;
import dev.zenqrt.mso.proxy.MSOProxy;
import net.kyori.adventure.text.Component;

public final class MessageSender {

    private static final ProxyServer proxy = MSOProxy.getInstance().getServer();

    public static void sendToWithPermission(String permission, Component message) {
        proxy.getAllPlayers().stream()
                .filter(player -> player.hasPermission(permission))
                .forEach(player -> player.sendMessage(message));
    }

    public static void sendToOperators(Component message) {
        sendToWithPermission("mso.admin", message);
    }

}
