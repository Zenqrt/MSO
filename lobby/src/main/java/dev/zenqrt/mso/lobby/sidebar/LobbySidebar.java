package dev.zenqrt.mso.lobby.sidebar;

import dev.zenqrt.mso.sidebar.SidebarTexts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.scoreboard.Sidebar;

public final class LobbySidebar extends Sidebar {

    public LobbySidebar(String nextGame, int onlineCount, int score) {
        super(Component.text("ᴍɪɴɪ ", NamedTextColor.GREEN).decorate(TextDecoration.BOLD)
                .append(Component.text("sǫᴜɪᴅ ", NamedTextColor.YELLOW))
                .append(Component.text("ᴏʟʏᴍᴘɪᴄs", NamedTextColor.RED)));

        createLine(new ScoreboardLine("next_game", createNextGameComponent(nextGame), 3));
        createLine(new ScoreboardLine("online_count", createOnlineCountComponent(onlineCount), 2));
        createLine(new ScoreboardLine("empty1", Component.empty(), 2));
        createLine(new ScoreboardLine("player_score", SidebarTexts.playerScore(score), 0));
    }

    private static Component createNextGameComponent(String name) {
        return Component.text("ɴᴇxᴛ ɢᴀᴍᴇ: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(name, NamedTextColor.WHITE));
    }

    private static Component createOnlineCountComponent(int onlineCount) {
        return Component.text("ᴘʟᴀʏᴇʀs ᴏɴʟɪɴᴇ: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text(onlineCount, NamedTextColor.WHITE));
    }

    public void updateNextGame(String nextGame) {
        updateLineContent("next_game", createNextGameComponent(nextGame));
    }

    public void updateOnlineCount(int onlineCount) {
        updateLineContent("online_count", createOnlineCountComponent(onlineCount));
    }

    public void updateScore(int score) {
        updateLineContent("player_score", SidebarTexts.playerScore(score));
    }

}
