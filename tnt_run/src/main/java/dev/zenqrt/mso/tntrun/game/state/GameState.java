package dev.zenqrt.mso.tntrun.game.state;

public abstract class GameState {

    private boolean active;

    protected void onStateStart() {}
    protected void onStateEnd() {}

    public final void start() {
        if (active)
            return;

        active = true;

        onStateStart();
    }

    public final boolean end() {
        if (!active)
            return false;

        active = false;

        onStateEnd();
        return true;
    }
}
