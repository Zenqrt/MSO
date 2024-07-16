package dev.zenqrt.mso.lobby.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Metadata;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class PodiumNPC extends NPC {

    public PodiumNPC(String username, PlayerSkin skin) {
        super(username, skin);
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        super.updateNewViewer(player);

        if (player.getUsername().equals(getUsername())) {
            createHighlightTeam(player);
            EntityMetaDataPacket packet = new EntityMetaDataPacket(getEntityId(), Map.of(0, Metadata.Byte((byte) 64)));
            player.sendPacket(packet);
        }
    }

    private void createHighlightTeam(Player player) {
        TeamsPacket creationPacket = new TeamsPacket("highlight", new TeamsPacket.CreateTeamAction(
                Component.empty(),
                (byte) 0,
                TeamsPacket.NameTagVisibility.NEVER,
                TeamsPacket.CollisionRule.NEVER,
                NamedTextColor.AQUA,
                Component.empty(),
                Component.empty(),
                List.of(getInternalUsername())
        ));

        player.sendPacket(creationPacket);
    }
}
