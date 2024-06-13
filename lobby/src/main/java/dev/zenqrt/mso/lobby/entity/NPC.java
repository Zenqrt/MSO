package dev.zenqrt.mso.lobby.entity;

import net.minestom.server.entity.*;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class NPC extends Entity {

    private final String username;
    private final PlayerSkin skin;

    public NPC(String username, PlayerSkin skin) {
        super(EntityType.PLAYER);

        this.username = username;
        this.skin = skin;
    }

    @Override
    public void tick(long time) {
        super.tick(time);

        instance.getNearbyEntities(position, 4).stream()
                .filter(entity -> entity instanceof Player)
                .findFirst()
                .ifPresent(this::lookAt);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void updateNewViewer(@NotNull Player player) {
        PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(
                getUuid(),
                username,
                List.of(new PlayerInfoUpdatePacket.Property("textures", skin.textures(), skin.signature())),
                false,
                0,
                GameMode.SURVIVAL,
                null,
                null
        );
        PlayerInfoUpdatePacket packet = new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry);
        player.sendPacket(packet);
        super.updateNewViewer(player);

        player.sendPacket(new EntityMetaDataPacket(getEntityId(), Map.of(17, Metadata.Byte((byte) 127))));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);
        player.sendPacket(new PlayerInfoRemovePacket(getUuid()));
    }
}
