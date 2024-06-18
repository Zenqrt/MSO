package dev.zenqrt.mso.lobby.entity;

import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;

public final class NPCLookAt extends NPC {

    public NPCLookAt(String username, PlayerSkin skin) {
        super(username, skin);
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        instance.getNearbyEntities(position, 4).stream()
                .filter(entity -> entity instanceof Player)
                .findFirst()
                .ifPresent(this::lookAt);
    }
}
