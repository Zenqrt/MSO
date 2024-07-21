package dev.zenqrt.mso.match.game.sidebar;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.match.game.player.MatchPlayer;
import dev.zenqrt.mso.sidebar.SidebarTexts;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.scoreboard.Sidebar;

import java.util.Comparator;
import java.util.List;

public final class MatchSidebar extends Sidebar {

    public MatchSidebar(String mapName) {
        super(Component.text("ᴍᴀᴛᴄʜ: ", NamedTextColor.YELLOW)
                .append(Component.text("ғʀᴇɴᴢʏ", NamedTextColor.AQUA)).decorate(TextDecoration.BOLD));

        createLine(new ScoreboardLine("map", Component.text("ᴍᴀᴘ: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(mapName, NamedTextColor.WHITE)), 8));
        createLine(new ScoreboardLine("empty1", Component.empty(), 7));
        createLine(new ScoreboardLine("leaderboard_header", Component.text("ᴛᴏᴘ ᴄᴏᴍᴘʟᴇᴛᴇᴅ", NamedTextColor.LIGHT_PURPLE), 6));
        createLine(new ScoreboardLine("first_place", createEmptyLeaderboardComponent(1), 5));
        createLine(new ScoreboardLine("second_place", createEmptyLeaderboardComponent(2), 4));
        createLine(new ScoreboardLine("third_place", createEmptyLeaderboardComponent(3), 3));
        createLine(new ScoreboardLine("empty2", Component.empty(), 2));
        createLine(new ScoreboardLine("player_builds_completed", createBuildsCompletedComponent(0), 1));
        createLine(new ScoreboardLine("player_score", SidebarTexts.playerScore(0), 0));
    }

    public void updateLeaderboard(GamePlayerList<MatchPlayer> playerList) {
        List<Pair<String, Integer>> topUsernames = playerList.getPlayers().values().stream()
                .map(player -> Pair.of(player.player().getUsername(), player.buildsCompleted()))
                .filter(playerInfo -> playerInfo.value() > 0)
                .sorted(Comparator.comparing(Pair::value, (score, otherScore) -> Integer.compare(otherScore, score)))
                .limit(3)
                .toList();

        updateLeaderboardLine("first_place", 0, topUsernames);
        updateLeaderboardLine("second_place", 1, topUsernames);
        updateLeaderboardLine("third_place", 2, topUsernames);
    }

    private void updateLeaderboardLine(String id, int index, List<Pair<String, Integer>> topUsernames) {
        int placement = index + 1;

        if (index >= topUsernames.size())
            updateLineContent(id, createEmptyLeaderboardComponent(placement));
        else
            updateLineContent(id, createLeaderboardComponent(placement, topUsernames.get(index)));
    }

    private static Component createEmptyLeaderboardComponent(int placement) {
        return SidebarTexts.placement(placement, Component.text("...", NamedTextColor.DARK_GRAY));
    }

    private static Component createLeaderboardComponent(int placement, Pair<String, Integer> playerInfo) {
        return SidebarTexts.placement(
                placement,
                Component.text(playerInfo.key(), NamedTextColor.WHITE)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(playerInfo.value(), NamedTextColor.WHITE)));
    }

    public void updateScore(int score) {
        updateLineContent("player_score", SidebarTexts.playerScore(score));
    }

    public void updateBuildsCompleted(int buildsCompleted) {
        updateLineContent("player_builds_completed", createBuildsCompletedComponent(buildsCompleted));
    }

    private static Component createBuildsCompletedComponent(int buildsCompleted) {
        return Component.text("ʙᴜɪʟᴅs ᴄᴏᴍᴘʟᴇᴛᴇᴅ: ", NamedTextColor.GREEN)
                .append(Component.text(buildsCompleted, NamedTextColor.WHITE));
    }

}
