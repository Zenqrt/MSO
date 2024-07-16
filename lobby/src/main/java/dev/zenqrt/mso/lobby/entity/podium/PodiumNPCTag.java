package dev.zenqrt.mso.lobby.entity.podium;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class PodiumNPCTag extends Entity {

    private String username;

    public PodiumNPCTag(String username) {
        super(EntityType.TEXT_DISPLAY);

        this.username = username;

        editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(Component.text(username));
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setHasNoGravity(true);
        });
    }

    public void updateUsername(String username) {
        this.username = username;
        editEntityMeta(TextDisplayMeta.class, meta -> meta.setText(Component.text(username)));
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        player.sendPacket(this.getEntityType().registry().spawnType().getSpawnPacket(this));

        if (player.getUsername().equals(username)) {
            Map<Integer, Metadata.Entry<?>> entries = new HashMap<>(metadata.getEntries());
            entries.replace(23, Metadata.Chat(Component.text(username, NamedTextColor.AQUA).decorate(TextDecoration.BOLD)));

            EntityMetaDataPacket packet = new EntityMetaDataPacket(getEntityId(), Map.copyOf(entries));
            player.sendPacket(packet);
        } else {
            player.sendPacket(getMetadataPacket());
        }
    }
}
