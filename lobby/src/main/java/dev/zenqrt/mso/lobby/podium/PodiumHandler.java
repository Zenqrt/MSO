package dev.zenqrt.mso.lobby.podium;

import dev.zenqrt.mso.lobby.messenger.responses.ScoreResponse;
import net.minestom.server.utils.validate.Check;

public final class PodiumHandler {

    private final PodiumDisplay[] displays;

    public PodiumHandler(PodiumDisplay[] displays) {
        Check.argCondition(displays.length != 3, "displays should be length of 3");
        this.displays = displays;
    }

    public void updatePodiums(ScoreResponse[] responses) {
        Check.argCondition(responses.length > 3, "responses should be length of 3");

        for (int i = 0; i < responses.length; i++) {
            ScoreResponse response = responses[i];
            displays[i].update(response.username(), response.skin(), response.score());
        }
    }
}
