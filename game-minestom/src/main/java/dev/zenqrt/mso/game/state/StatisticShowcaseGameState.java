package dev.zenqrt.mso.game.state;

import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.task.TaskManager;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;

public final class StatisticShowcaseGameState extends GameState {

    private final GamePlayerList<? extends MinestomGamePlayer> playerList;
    private final ScoreKeeper scoreKeeper;
    private final TaskManager taskManager;

    public StatisticShowcaseGameState(GamePlayerList<? extends MinestomGamePlayer> playerList, ScoreKeeper scoreKeeper) {
        this.playerList = playerList;
        this.scoreKeeper = scoreKeeper;
        this.taskManager = new TaskManager(MinecraftServer.getSchedulerManager());
    }

     @Override
    protected void onStateStart() {
        playerList.forEach(gamePlayer -> {
            Player player = gamePlayer.player();

            player.setGameMode(GameMode.SPECTATOR);
            player.setInvisible(true);
            player.showTitle(Title.title(
                    Component.text("GAME END!", NamedTextColor.RED).decorate(TextDecoration.BOLD),
                    Component.empty(),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO)
            ));
            player.playSound(Sound.sound(SoundEvent.ITEM_TRIDENT_RETURN, Sound.Source.MASTER, 1, 0), Sound.Emitter.self());
        });

        taskManager.startTask(() ->
                playerList.getPlayersAsAudience().sendMessage(Component.text("There is supposed to be a message that displays top stats but I haven't done it yet so pretend this is it :)")),
                TaskSchedule.seconds(3), TaskSchedule.stop());

//        List<GamePlayer> topPlayers = scoreKeeper.getScoresGained().entrySet().stream()
//                .sorted(Map.Entry.comparingByValue((score, otherScore) -> Integer.compare(otherScore, score)))
//                .limit(3)
//                .map(entry -> playerList.getPlayer(entry.getKey()))
//                .collect(Collectors.toList());
//
//        for (int i = 0; i < topPlayers.size(); i++)
//            showPlacementTitle(topPlayers.get(i), i);

         taskManager.startTask(this::notifyEnd, TaskSchedule.seconds(10), TaskSchedule.stop());
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

    @Override
    protected void onStateEnd() {
        taskManager.shutdownAllTasks();
    }
}
