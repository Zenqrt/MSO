package dev.zenqrt.mso.proxy.messages;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.exception.PluginMessageException;
import dev.zenqrt.mso.proxy.game.MSOGame;

public final class ServerTransferHandler {

    private final MSOProxy plugin;
    private final MSOGame game;

    public ServerTransferHandler(MSOProxy plugin, MSOGame game) {
        this.plugin = plugin;
        this.game = game;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Subscribe
    public void onPluginMessageFromPlayer(PluginMessageEvent event) {
        if (!(event.getSource() instanceof Player player && event.getIdentifier() == ChannelIdentifiers.GAME_TRANSFER))
            return;

        ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
        String callType = input.readLine();

        switch (callType) {
            case "lobby" -> player.createConnectionRequest(game.getLobbyServer());
            case "current_game" -> player.createConnectionRequest(game.getCurrentGame().server());
            default -> {
                event.setResult(PluginMessageEvent.ForwardResult.handled());
                throw new PluginMessageException("Invalid call type");
            }
        }
    }

}
