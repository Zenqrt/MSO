package dev.zenqrt.mso.proxy.game.state.states;

import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.messenger.ConnectionSettings;
import dev.zenqrt.mso.proxy.game.MSOGame;

public final class WaitingForServerGameState extends GameState {

    private final MSOGame game;
    private final Thread serverReadyListener;

    public WaitingForServerGameState(MSOGame game) {
        this.serverReadyListener = ConnectionSettings.createMessageReceiveListener(game.getGameTransferChannelReceiver(), _ -> game.switchToNextState());
        this.game = game;
    }

    @Override
    protected void onStateStart() {
        game.getInfoChannelSender().sendMessageAsync(game.getCurrentGame().serverId(), output -> {
            output.writeUTF("player_count");
            output.writeInt(game.getPlayerList().getPlayers().size());
        });
        serverReadyListener.start();
    }

    @Override
    protected void onStateEnd() {
        serverReadyListener.interrupt();
    }
}
