package dev.zenqrt.mso.parkourrace.sidebar;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.utils.Texts;
import dev.zenqrt.mso.parkourrace.game.player.ParkourRacePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.scoreboard.Sidebar;

import java.util.Comparator;
import java.util.List;

public final class ParkourRaceSidebar extends Sidebar {

    private final int maxCheckpoints;

    public ParkourRaceSidebar(int maxCheckpoints) {
        super(Component.text("ᴘᴀʀᴋᴏᴜʀ ʀᴀᴄᴇ", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));

        this.maxCheckpoints = maxCheckpoints;

        createLine(new ScoreboardLine("map", Component.text("ᴍᴀᴘ: ", NamedTextColor.LIGHT_PURPLE)
                .append(Component.text("Into the Miniverse", NamedTextColor.WHITE)), 7));
        createLine(new ScoreboardLine("empty2", Component.empty(), 6));
        createLine(new ScoreboardLine("leaderboard_header", Component.text("sᴛᴀɴᴅɪɴɢs", NamedTextColor.LIGHT_PURPLE), 5));
        createLine(new ScoreboardLine("first_place", getEmptyStandingComponent(1), 4));
        createLine(new ScoreboardLine("second_place", getEmptyStandingComponent(2), 3));
        createLine(new ScoreboardLine("third_place", getEmptyStandingComponent(3), 2));
        createLine(new ScoreboardLine("empty1", Component.empty(), 1));
        createLine(new ScoreboardLine("player_checkpoint", createPlayerCheckpointComponent(0), 0));
    }

    public void updateStandings(GamePlayerList<ParkourRacePlayer> playerList) {
        List<ParkourRacePlayer> topPlayers = playerList.getPlayers().values().stream()
                .sorted(Comparator.comparing(ParkourRacePlayer::checkpointNumber, (checkpoint, otherCheckpoint) -> Integer.compare(otherCheckpoint, checkpoint)))
                .limit(3)
                .toList();

        updateStandingLine("first_place", 0, topPlayers);
        updateStandingLine("second_place", 1, topPlayers);
        updateStandingLine("third_place", 2, topPlayers);
    }

    private void updateStandingLine(String id, int index, List<ParkourRacePlayer> topUsernames) {
        int placement = index + 1;

        if (index >= topUsernames.size())
            updateLineContent(id, getEmptyStandingComponent(placement));
        else
            updateLineContent(id, getStandingComponent(placement, topUsernames.get(index)));
    }

    private static Component getEmptyStandingComponent(int placement) {
        return Texts.placement(placement, Component.text("...", NamedTextColor.DARK_GRAY));
    }

    private Component getStandingComponent(int placement, ParkourRacePlayer gamePlayer) {
        Component component = Component.text(gamePlayer.player().getUsername(), NamedTextColor.WHITE)
                .append(Component.text(" - ", NamedTextColor.DARK_GRAY));

        int checkpointNumber = gamePlayer.checkpointNumber();

        if (checkpointNumber > maxCheckpoints) {
            component = component.append(Component.text("DONE", NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
        } else {
            component = component.append(Component.text("#" + gamePlayer.checkpointNumber(), NamedTextColor.YELLOW));
        }
        return Texts.placement(placement, component);
    }

    public void updateCheckpoint(int checkpoint) {
        updateLineContent("player_checkpoint", createPlayerCheckpointComponent(checkpoint));
    }

    private Component createPlayerCheckpointComponent(int checkpoint) {
        return Component.text("ᴄʜᴇᴄᴋᴘᴏɪɴᴛ: {checkpoint}", NamedTextColor.GREEN)
                .replaceText(builder ->
                        builder.matchLiteral("{checkpoint}")
                                .replacement(Component.text("#" + checkpoint + "/" + maxCheckpoints, NamedTextColor.WHITE)));
    }
}
