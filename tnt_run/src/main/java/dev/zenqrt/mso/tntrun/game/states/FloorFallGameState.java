package dev.zenqrt.mso.tntrun.game.states;

import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.game.task.TaskManager;
import dev.zenqrt.mso.game.text.GameMessages;
import dev.zenqrt.mso.game.text.Titles;
import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.player.TNTRunPlayer;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class FloorFallGameState extends EventGameState {

    private final TNTRunGame game;
    private final TNTRunConfig config;
    private final List<Player> playersLeft;
    private final List<GamePlayer> topPlayers = new ArrayList<>(3);
    private final Map<UUID, Task> blockBreakingTasks = new HashMap<>();
    private final TaskManager taskManager;

    public FloorFallGameState(EventNode<Event> parentNode, TNTRunGame game, TNTRunConfig config) {
        super(parentNode);

        this.game = game;
        this.config = config;
        this.playersLeft = new ArrayList<>();
        this.taskManager = new TaskManager(MinecraftServer.getSchedulerManager());
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> playersLeft.contains(event.getPlayer()))
                .filter(event -> event.getNewPosition().y() < config.bottomYLevel())
                .handler(event -> {
                    ScoreKeeper scoreKeeper = game.getScoreKeeper();
                    Player player = event.getPlayer();

                    eliminatePlayer(player);

                    topPlayers.addFirst(game.getPlayerList().getPlayer(player.getUuid()));
                    playersLeft.forEach(playerLeft -> scoreKeeper.addScore(playerLeft.getUuid(), playerLeft, 2, "Survival"));

                    if (playersLeft.size() == 1) {
                        topPlayers.addFirst(game.getPlayerList().getPlayer(playersLeft.getFirst().getUuid()));
                        scoreKeeper.addPlacementScores(topPlayers);
                        this.notifyEnd();
                        return;
                    }

                    Titles.sendDeathTitle(player);
                }).build());
    }

    private void eliminatePlayer(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.setInvisible(true);
        playersLeft.remove(player);

        UUID uuid = player.getUuid();
        blockBreakingTasks.get(uuid).cancel();
        blockBreakingTasks.remove(uuid);

        Audiences.players().sendMessage(GameMessages.death(player.getUsername(), "{username} has been eliminated!"));
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();

        taskManager.startTask(new StopwatchTask(), TaskSchedule.immediate(), TaskSchedule.seconds(1));
        playersLeft.addAll(game.getPlayerList().getPlayers().values().stream()
                .map(TNTRunPlayer::player)
                .toList());
        game.getPlayerList().forEach(gamePlayer -> blockBreakingTasks.put(
                gamePlayer.uuid(),
                MinecraftServer.getSchedulerManager().scheduleTask(new BlockRemoveTask(gamePlayer.player()),
                        TaskSchedule.immediate(), TaskSchedule.tick(1))));
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();
        taskManager.shutdownAllTasks();
        blockBreakingTasks.forEach((_, task) -> task.cancel());
    }

    private class StopwatchTask implements Runnable {

        private int timeElapsed;

        @Override
        public void run() {
            game.getPlayerList().getPlayersAsAudience().sendActionBar(Component.text(formatTime(timeElapsed), TextColor.color(0xd6faff))
                    .append(Component.text(" ⌚", NamedTextColor.GOLD)));

            timeElapsed++;
        }

        private static String formatTime(int time) {
            int minutes = time / 60;
            int seconds = time % 60;

            return "%d:%02d".formatted(minutes, seconds);
        }
    }

    private static class BlockRemoveTask implements Runnable {

        private final Player player;
        private final List<Point> blocksToRemove = new ArrayList<>();
        private int ticksOnAir;

        BlockRemoveTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            Pos position = player.getPosition().sub(0, 2, 0);
            Point blockPosition = position.asVec().apply(Vec.Operator.FLOOR);

            Instance instance = player.getInstance();

            if (!blocksToRemove.contains(blockPosition)) {
                if (instance.getBlock(position).compare(Block.TNT)) {
                    ticksOnAir = 0;
                    blocksToRemove.add(blockPosition);

                    MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                        removeBlocks(instance, blockPosition);
                        blocksToRemove.remove(blockPosition);
                    }, TaskSchedule.tick(player.isSprinting() ? 5 : 7), TaskSchedule.stop());

                    return;
                }
            } else {
                return;
            }

            if (ticksOnAir++ <= 5 || !player.isOnGround()) {
                return;
            }

            Point tntPosition = tntBlockPosition(instance, position);

            if (tntPosition == null)
                return;

            removeBlocks(instance, tntPosition);
        }

        private static void removeBlocks(Instance instance, Point tntBlockPoint) {
            instance.setBlock(tntBlockPoint, Block.AIR);
            instance.setBlock(tntBlockPoint.add(0, 1, 0), Block.AIR);
        }

        private static @Nullable Point tntBlockPosition(Instance instance, Point originPoint) {
            double error = 0.3;

            List<Point> points = List.of(
                    originPoint.add(error, 0, error),
                    originPoint.add(-error, 0, error),
                    originPoint.add(error, 0, -error),
                    originPoint.add(-error, 0, -error)
            );

            return points.stream()
                    .map(point -> new Vec(point.blockX(), point.blockY(), point.blockZ()))
                    .distinct()
                    .filter(point -> !point.sameBlock(originPoint))
                    .filter(point -> instance.getBlock(point).compare(Block.TNT))
                    .min(Comparator.comparingDouble(point -> point.distance(originPoint)))
                    .orElse(null);
        }

    }
}
