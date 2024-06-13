package dev.zenqrt.mso.proxy.game.state;

import com.velocitypowered.api.event.EventManager;
import dev.zenqrt.mso.proxy.MSOProxy;

public class EventGameState extends GameState {

    protected final MSOProxy plugin;
    private final EventManager eventManager;

    public EventGameState(MSOProxy plugin) {
        this.plugin = plugin;
        this.eventManager = plugin.getServer().getEventManager();
    }

    @Override
    protected void onStateStart() {
        eventManager.register(plugin, this);
    }

    @Override
    protected void onStateEnd() {
        eventManager.unregisterListener(plugin, this);
    }
}
