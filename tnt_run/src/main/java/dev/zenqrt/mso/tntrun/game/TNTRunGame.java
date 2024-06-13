package dev.zenqrt.mso.tntrun.game;

import dev.zenqrt.mso.tntrun.map.TNTRunConfig;
import net.minestom.server.instance.Instance;

public final class TNTRunGame extends GameState {

    private final Instance instance;
    private final TNTRunConfig config;

    public TNTRunGame(Instance instance, TNTRunConfig config) {
        this.instance = instance;
        this.config = config;
    }

    public Instance getInstance() {
        return instance;
    }

    public TNTRunConfig getConfig() {
        return config;
    }
}
