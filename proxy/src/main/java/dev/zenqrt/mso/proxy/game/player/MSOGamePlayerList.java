package dev.zenqrt.mso.proxy.game.player;

import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.proxy.game.MSOGame;

public final class MSOGamePlayerList extends HashMapGamePlayerList<MSOGamePlayer> {

    private final MSOGame game;

    public MSOGamePlayerList(MSOGame game) {
        this.game = game;
    }
}
