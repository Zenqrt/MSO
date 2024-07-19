package dev.zenqrt.mso.parkourrace.game.states;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.game.task.TaskManager;
import dev.zenqrt.mso.parkourrace.game.player.ParkourRacePlayer;
import dev.zenqrt.mso.parkourrace.sidebar.ParkourRaceSidebar;
import dev.zenqrt.mso.text.TextColorPresets;
import it.unimi.dsi.fastutil.Pair;
import map.ParkourRaceConfig;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public final class RaceGameState extends EventGameState {

    private final GamePlayerList<ParkourRacePlayer> playerList;
    private final ParkourRaceConfig config;
    private final ScoreKeeper scoreKeeper;
    private final Map<Player, ParkourRaceSidebar> sidebars;
    private final TaskManager taskManager;
    private final Set<UUID> finished = new HashSet<>();
    private TimerTask timerTask;

    public RaceGameState(EventNode<Event> parentNode, GamePlayerList<ParkourRacePlayer> playerList, ParkourRaceConfig config, ScoreKeeper scoreKeeper, Map<Player, ParkourRaceSidebar> sidebars) {
        super(parentNode);

        this.playerList = playerList;
        this.config = config;
        this.scoreKeeper = scoreKeeper;
        this.sidebars = sidebars;
        this.taskManager = new TaskManager(MinecraftServer.getSchedulerManager());
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> event.getNewPosition().y() <= 0)
                .handler(event -> {
                    Player player = event.getPlayer();

                    player.teleport(player.getRespawnPoint());
                    player.playSound(Sound.sound(SoundEvent.ENTITY_ENDERMAN_TELEPORT, Sound.Source.MASTER, 1, 2), Sound.Emitter.self());
                })
                .build());
        eventNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> !finished.contains(event.getPlayer().getUuid()))
                .handler(event -> {
                    Player player = event.getPlayer();
                    UUID uuid = player.getUuid();
                    ParkourRacePlayer gamePlayer = playerList.getPlayer(uuid);
                    Pos position = event.getNewPosition();

                    if (gamePlayer.checkpointNumber() == config.checkpoints().length) {
                        if (!position.sameBlock(config.finish()))
                            return;

                        finished.add(uuid);
                        player.setGameMode(GameMode.SPECTATOR);

                        int placeFinished = finished.size();

                        switch (placeFinished) {
                            case 1 -> scoreKeeper.addFirstPlaceScore(uuid, player);
                            case 2 -> scoreKeeper.addSecondPlaceScore(uuid, player);
                            case 3 -> scoreKeeper.addThirdPlaceScore(uuid, player);
                            default -> scoreKeeper.addScore(uuid, player, 3, getPlaceSuffix(placeFinished));
                        }

                        playerList.updatePlayer(player.getUuid(), ParkourRacePlayer::addCheckpointNumber);
                        sidebars.forEach((_, sidebar) -> sidebar.updateStandings(playerList));

                        Component finishMessage = Component.text("{player} has finished the parkour in {place}!", TextColorPresets.TEXT)
                                .replaceText(builder ->
                                        builder.matchLiteral("{player}")
                                        .replacement(Component.text(player.getUsername(), NamedTextColor.WHITE)))
                                .replaceText(builder ->
                                        builder.matchLiteral("{place}")
                                                .replacement(Component.text(placeFinished + getPlaceSuffix(placeFinished) + " place", NamedTextColor.AQUA)));

                        if (timerTask.timeLeft > 60 && placeFinished == 1) {
                            finishMessage = finishMessage.append(Component.text(" Timer has shortened to 60 seconds.", NamedTextColor.GRAY));
                            timerTask.timeLeft = 60;
                        }

                        Audience audience = playerList.getPlayersAsAudience();
                        audience.sendMessage(finishMessage);
                        audience.playSound(Sound.sound(SoundEvent.ENTITY_PLAYER_LEVELUP, Sound.Source.MASTER, 1, 1.25F), Sound.Emitter.self());

                        if (finished.containsAll(playerList.getPlayers().keySet()))
                            notifyEnd();

                        return;
                    }

                    Pair<Integer, Pos> checkpoint = findCheckpoint(position, config.checkpoints());

                    if (checkpoint == null)
                        return;

                    int checkpointNumber = checkpoint.key();

                    if (gamePlayer.checkpointNumber() >= checkpointNumber)
                        return;

                    long now = System.currentTimeMillis();

                    player.setRespawnPoint(checkpoint.value().add(0.5, 0, 0.5));
                    playerList.getPlayersAsAudience().sendMessage(Component.text("{player} has reached checkpoint {checkpoint}!", NamedTextColor.GRAY)
                            .replaceText(builder ->
                                    builder.matchLiteral("{player}")
                                            .replacement(Component.text(player.getUsername(), NamedTextColor.WHITE)))
                            .replaceText(builder ->
                                    builder.matchLiteral("{checkpoint}")
                                            .replacement(Component.text("#" + checkpointNumber, NamedTextColor.YELLOW))));

                    scoreKeeper.addScore(uuid, player, 1, "Checkpoint");

                    ParkourRacePlayer updatedPlayer = gamePlayer.withCheckpointNumber(checkpointNumber);
                    playerList.updatePlayer(updatedPlayer);

                    sidebars.get(player).updateCheckpoint(updatedPlayer.checkpointNumber());
                    sidebars.forEach((_, sidebar) -> sidebar.updateStandings(playerList));
                }).build());
    }

    private static String getPlaceSuffix(int place) {
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

    private static @Nullable Pair<Integer, Pos> findCheckpoint(Point playerPosition, Pos[] checkpoints) {
        for (int i = 0; i < checkpoints.length; i++) {
            Pos checkpoint = checkpoints[i];

            if (playerPosition.sameBlock(checkpoint))
                return Pair.of(i + 1, checkpoint);
        }

        return null;
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        sidebars.forEach((_, sidebar) -> sidebar.updateStandings(playerList));
        playerList.forEach(player -> player.player().setInvisible(true));

        this.timerTask = new TimerTask(900);
        taskManager.startTask(timerTask, TaskSchedule.immediate(), TaskSchedule.seconds(1));

        Audience audience = playerList.getPlayersAsAudience();
        audience.showTitle(Title.title(
                Component.text("GO!", NamedTextColor.AQUA).decorate(TextDecoration.BOLD),
                Component.empty(),
                Title.Times.times(Duration.ZERO, Ticks.duration(50), Duration.ofSeconds(1))
        ));
        audience.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1, 2), Sound.Emitter.self());
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();
        taskManager.shutdownAllTasks();
    }

    private class TimerTask implements Runnable {

        private int timeLeft;

        TimerTask(int time) {
            this.timeLeft = time;
        }

        @Override
        public void run() {
            if (timeLeft <= 0) {
                notifyEnd();
                return;
            }
            playerList.getPlayersAsAudience().sendActionBar(Component.text(formatTime(timeLeft), TextColor.color(0xd6faff))
                    .append(Component.text(" ⌚", NamedTextColor.GOLD)));
            timeLeft--;
        }

        private static String formatTime(int time) {
            int minutes = time / 60;
            int seconds = time % 60;

            return "%d:%02d".formatted(minutes, seconds);
        }
    }
}
