package dev.zenqrt.mso.match.game.states.pregame;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.match.game.player.MatchPlayer;
import dev.zenqrt.mso.match.utils.sidebar.SidebarUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.scoreboard.Sidebar;

public final class PregameSidebarGameState extends EventGameState {

    private final GamePlayerList<MatchPlayer> playerList;
    private final Sidebar sidebar;

    public PregameSidebarGameState(EventNode<Event> parentNode, GamePlayerList<MatchPlayer> playerList) {
        super(parentNode);

        this.playerList = playerList;
        this.sidebar = SidebarUtils.createGameSidebar();

        this.sidebar.createLine(new Sidebar.ScoreboardLine("empty1", Component.space(), 2));
        this.sidebar.createLine(new Sidebar.ScoreboardLine("waiting", Component.text("sᴛᴀʀᴛɪɴɢ sᴏᴏɴ...", NamedTextColor.GRAY), 1));
        this.sidebar.createLine(new Sidebar.ScoreboardLine("empty2", Component.space(), 0));
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(PlayerSpawnEvent.class, event -> sidebar.addViewer(event.getPlayer()));
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        playerList.forEach(gamePlayer -> sidebar.addViewer(gamePlayer.player()));
    }

    @Override
    protected void onStateEnd() {
        sidebar.getViewers().forEach(sidebar::removeViewer);
    }
}
