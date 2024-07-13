package dev.zenqrt.mso.game.state;

import org.jetbrains.annotations.ApiStatus;

public class GameState {

    private boolean active;
    private boolean readyToEnd;

    protected void onStateStart() {}
    protected void onStateEnd() {}

    public final void notifyEnd() {
        readyToEnd = true;
    }

    @ApiStatus.Internal
    public final void start() {
        if (active)
            return;

        active = true;

        onStateStart();
    }

    @ApiStatus.Internal
    public final boolean end() {
        if (!active)
            return false;

        active = false;

        onStateEnd();
        return true;
    }

    @ApiStatus.Internal
    public final boolean isActive() {
        return active;
    }

    public boolean isReadyToEnd() {
        return readyToEnd;
    }
}
