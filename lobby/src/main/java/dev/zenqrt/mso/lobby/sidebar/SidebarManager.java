package dev.zenqrt.mso.lobby.sidebar;

import dev.zenqrt.mso.lobby.data.cache.CachedValues;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SidebarManager {

    private final Map<UUID, LobbySidebar> sidebars = new HashMap<>();

    public void init(EventNode<Event> eventNode) {
        eventNode.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            UUID uuid = player.getUuid();
            int onlineCount = MinecraftServer.getConnectionManager().getOnlinePlayerCount();

            LobbySidebar sidebar = new LobbySidebar(CachedValues.getNextGame(), onlineCount, CachedValues.getScore(uuid));
            sidebar.addViewer(player);
            sidebars.put(uuid, sidebar);
            
            sidebars.forEach((_, sb) -> sb.updateOnlineCount(onlineCount));
        });
    }

    public LobbySidebar getSidebar(UUID uuid) {
        return sidebars.get(uuid);
    }

    public Map<UUID, LobbySidebar> getSidebars() {
        return sidebars;
    }
}
