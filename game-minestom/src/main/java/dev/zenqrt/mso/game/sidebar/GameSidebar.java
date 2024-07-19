package dev.zenqrt.mso.game.sidebar;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.utils.Texts;
import dev.zenqrt.mso.text.Icons;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.scoreboard.Sidebar;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public final class GameSidebar extends Sidebar {

    public GameSidebar(@NotNull Component title) {
        super(title.decorate(TextDecoration.BOLD));

        createLine(new Sidebar.ScoreboardLine("leaderboard_header", Component.text("ʀᴏᴜɴᴅ sᴄᴏʀᴇ", NamedTextColor.LIGHT_PURPLE), 5));
        createLine(new Sidebar.ScoreboardLine("first_place", getEmptyLeaderboardComponent(1), 4));
        createLine(new Sidebar.ScoreboardLine("second_place", getEmptyLeaderboardComponent(2), 3));
        createLine(new Sidebar.ScoreboardLine("third_place", getEmptyLeaderboardComponent(3), 2));
        createLine(new ScoreboardLine("empty2", Component.empty(), 1));
        createLine(new ScoreboardLine("player_score", Texts.playerScore(0), 0));
    }

    public void updateLeaderboard(GamePlayerList<? extends MinestomGamePlayer> playerList, ScoreKeeper scoreKeeper) {
        List<Pair<String, Integer>> topUsernames = playerList.getPlayers().values().stream()
                .map(player -> Pair.of(player.player().getUsername(), scoreKeeper.getScore(player.player().getUuid())))
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
            updateLineContent(id, getEmptyLeaderboardComponent(placement));
        else
            updateLineContent(id, getLeaderboardComponent(placement, topUsernames.get(index)));
    }

    private static Component getEmptyLeaderboardComponent(int placement) {
        return Texts.placement(placement, Component.text("...", NamedTextColor.DARK_GRAY));
    }

    private static Component getLeaderboardComponent(int placement, Pair<String, Integer> playerInfo) {
        return Texts.placement(
                placement,
                Component.text(playerInfo.key(), NamedTextColor.WHITE)
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(playerInfo.value(), NamedTextColor.WHITE)
                                .append(Component.space())
                                .append(Icons.SCORE)));
    }

    public void updateScore(int score) {
        updateLineContent("player_score", Texts.playerScore(score));
    }
}
