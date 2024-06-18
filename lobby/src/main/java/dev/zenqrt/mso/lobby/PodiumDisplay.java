package dev.zenqrt.mso.lobby;

import dev.zenqrt.mso.lobby.entity.NPC;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.Instance;

public final class PodiumDisplay {

    private final NPC npc;
    private final Entity textDisplay;

    public PodiumDisplay(NPC npc, Entity textDisplay) {
        this.npc = npc;
        this.textDisplay = textDisplay;
    }

    public void show(Instance instance, Point npcPosition, Point textDisplayPosition) {
        npc.setInstance(instance, npcPosition);
        textDisplay.setInstance(instance, textDisplayPosition);
    }

    public void update(String username, PlayerSkin skin) {
        npc.setUsername(username);
        npc.setSkin(skin);
    }
}
