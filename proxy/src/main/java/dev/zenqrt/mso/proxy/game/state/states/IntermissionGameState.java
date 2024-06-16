package dev.zenqrt.mso.proxy.game.state.states;

import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import dev.zenqrt.mso.messages.ChannelIdentifiers;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.state.EventGameState;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;

public final class IntermissionGameState extends EventGameState {

    private final MSOGame game;

    public IntermissionGameState(MSOProxy plugin, MSOGame game) {
        super(plugin);
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();

        ConnectionUtils.sendAllPlayersToServer(plugin.getServer(), game.getLobbyServer());
        game.getLobbyServer().sendPluginMessage(MinecraftChannelIdentifier.from(ChannelIdentifiers.INFO), output -> {
            output.writeUTF("next_game");
            output.writeUTF(game.getCurrentGame().displayName());
        });
    }
}
