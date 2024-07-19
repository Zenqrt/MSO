package dev.zenqrt.mso.game.state.pregame;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import dev.zenqrt.mso.game.state.EventGameState;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.scoreboard.Sidebar;

import java.util.Map;
import java.util.function.Supplier;

public final class DisplaySidebarGameState<T extends Sidebar> extends EventGameState {

    private final GamePlayerList<? extends MinestomGamePlayer> playerList;
    private final Map<Player, T> sidebars;
    private final Supplier<T> sidebarSupplier;

    public DisplaySidebarGameState(EventNode<Event> parentNode, GamePlayerList<? extends MinestomGamePlayer> playerList, Map<Player, T> sidebars, Supplier<T> sidebarSupplier) {
        super(parentNode);

        this.playerList = playerList;
        this.sidebars = sidebars;
        this.sidebarSupplier = sidebarSupplier;
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(PlayerSpawnEvent.class, event -> setSidebar(event.getPlayer()));
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        playerList.forEach(gamePlayer -> setSidebar(gamePlayer.player()));
    }

    private void setSidebar(Player player) {
        T sidebar = sidebarSupplier.get();
        sidebar.addViewer(player);
        sidebars.put(player, sidebar);
    }
}
