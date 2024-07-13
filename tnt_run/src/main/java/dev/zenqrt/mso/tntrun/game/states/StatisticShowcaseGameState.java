package dev.zenqrt.mso.tntrun.game.states;

import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.util.Map;

public final class StatisticShowcaseGameState extends GameState {

    private final TNTRunGame game;
    private final Map<Integer, GamePlayer> topPlayers;

    public StatisticShowcaseGameState(TNTRunGame game, Map<Integer, GamePlayer> topPlayers) {
        this.game = game;
        this.topPlayers = topPlayers;
    }

    @Override
    protected void onStateStart() {
        game.getPlayerList().forEach(gamePlayer -> {
            Player player = gamePlayer.player();

            player.setGameMode(GameMode.SPECTATOR);
            player.setInvisible(true);
        });

        topPlayers.forEach((place, gamePlayer) -> showPlacementTitle(gamePlayer, place));

        MinecraftServer.getSchedulerManager().scheduleTask(this::notifyEnd,
                TaskSchedule.seconds(10), TaskSchedule.stop());
    }

    private void showPlacementTitle(GamePlayer gamePlayer, int place) {
        if (gamePlayer == null)
            return;

        gamePlayer.player().showTitle(Title.title(Component.text("You finished in", NamedTextColor.GRAY),
                Component.text(place + getSuffix(place) + " place", NamedTextColor.YELLOW).decorate(TextDecoration.BOLD)));
    }

    private static String getSuffix(int place) {
        if (place >= 4 && place <= 20) {
            return "th";
        }

        int lastDigit = place % 10;

        if (lastDigit == 0 || lastDigit >= 4)
            return "th";
        else
            return switch (lastDigit) {
                case 1 -> "st";
                case 2 -> "nd";
                case 3 -> "rd";
                default -> throw new IllegalStateException("last digit is not a single digit");
            };
    }
}
