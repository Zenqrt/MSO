package dev.zenqrt.mso.match.game.states.pregame;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.match.game.player.MatchPlayer;
import dev.zenqrt.mso.match.utils.sidebar.SidebarUtils;
import dev.zenqrt.mso.match.utils.text.Texts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.scoreboard.Sidebar;

import java.util.Map;

public final class PregameSidebarGameState extends EventGameState {

    private final GamePlayerList<MatchPlayer> playerList;
    private final Map<Player, Sidebar> sidebars;

    public PregameSidebarGameState(EventNode<Event> parentNode, GamePlayerList<MatchPlayer> playerList, Map<Player, Sidebar> sidebars) {
        super(parentNode);

        this.playerList = playerList;
        this.sidebars = sidebars;
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
        Sidebar sidebar = createDefaultSidebar();
        sidebar.addViewer(player);
        sidebars.put(player, sidebar);
    }

    private static Sidebar createDefaultSidebar() {
        Sidebar sidebar = SidebarUtils.createGameSidebar();
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "header",
                Component.text("ʀᴏᴜɴᴅ sᴄᴏʀᴇ", NamedTextColor.LIGHT_PURPLE),
                8
        ));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "first_place",
                Texts.placement(1, Component.text("...", NamedTextColor.DARK_GRAY)),
                7
        ));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "second_place",
                Texts.placement(2, Component.text("...", NamedTextColor.DARK_GRAY)),
                6
        ));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "third_place",
                Texts.placement(3, Component.text("...", NamedTextColor.DARK_GRAY)),
                5
        ));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "empty",
                Component.empty(),
                4
        ));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "player_score",
                Texts.score(0),
                3
        ));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "player_stat",
                Texts.buildsCompleted(0),
                2
        ));
        sidebar.createLine(new Sidebar.ScoreboardLine(
                "empty2",
                Component.empty(),
                1
        ));

        return sidebar;
    }
}
