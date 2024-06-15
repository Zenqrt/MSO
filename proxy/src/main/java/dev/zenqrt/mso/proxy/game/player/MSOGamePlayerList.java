package dev.zenqrt.mso.proxy.game.player;

import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.state.states.IntermissionGameState;

public final class MSOGamePlayerList extends HashMapGamePlayerList<MSOGamePlayer> {

    private final MSOGame game;

    public MSOGamePlayerList(MSOGame game) {
        this.game = game;
    }

    @Override
    protected boolean canJoinGame() {
        return game.getState() instanceof IntermissionGameState;
    }
}
