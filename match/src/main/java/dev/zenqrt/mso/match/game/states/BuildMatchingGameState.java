package dev.zenqrt.mso.match.game.states;

import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.game.task.TaskManager;
import dev.zenqrt.mso.match.game.MatchGame;
import dev.zenqrt.mso.match.game.board.Board;
import dev.zenqrt.mso.match.game.board.Build;
import dev.zenqrt.mso.match.game.map.MatchSectionArea;
import dev.zenqrt.mso.match.game.player.MatchPlayer;
import dev.zenqrt.mso.match.utils.coordinate.Region;
import dev.zenqrt.mso.match.utils.sidebar.SidebarUtils;
import dev.zenqrt.mso.match.utils.text.Texts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.TransactionOption;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.validate.Check;

import java.util.*;

public final class BuildMatchingGameState extends EventGameState {

    private final MatchGame game;
    private final Build[] builds;
    private final Map<Player, Sidebar> sidebars = new HashMap<>();
    private final TaskManager taskManager;

    public BuildMatchingGameState(MatchGame game, Build[] builds) {
        super(game.getEventNode());

        this.game = game;
        this.builds = builds;
        this.taskManager = new TaskManager(MinecraftServer.getSchedulerManager());
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(ItemDropEvent.class, event -> event.setCancelled(true));
        game.getPlayerList().forEach(gamePlayer -> {
            Player player = gamePlayer.player();
            MatchSectionArea matchSection = game.getPlayerSections().get(player.getUuid());

            MatchSectionHandler handler = new MatchSectionHandler(gamePlayer, matchSection.displayBoard(), matchSection.placementBoard());
            eventNode.addChild(handler.createEventNode());

            handler.startBuild();
            handler.startTasks(taskManager);
        });
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        taskManager.startTask(new TimerTask(60), TaskSchedule.immediate(), TaskSchedule.seconds(1));

        game.getPlayerList().forEach(gamePlayer -> {
            Player player = gamePlayer.player();

            player.setGameMode(GameMode.SURVIVAL);
            player.setInstantBreak(true);

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

            sidebar.addViewer(player);
            sidebars.put(player, sidebar);
        });

    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();
        taskManager.shutdownAllTasks();
        game.getPlayerList().forEach(gamePlayer -> gamePlayer.player().setInstantBreak(false));

        sidebars.forEach((player, sidebar) -> sidebar.removeViewer(player));
        sidebars.clear();
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

            game.getPlayerList().getPlayersAsAudience().sendActionBar(Component.text(formatTime(timeLeft), TextColor.color(0xd6faff))
                    .append(Component.text(" ⌚", NamedTextColor.GOLD)));
            timeLeft--;
        }

        private static String formatTime(int time) {
            int minutes = time / 60;
            int seconds = time % 60;

            return "%d:%02d".formatted(minutes, seconds);
        }
    }

    private class MatchSectionHandler {

        private final Player player;
        private final Board displayBoard;
        private final Board placementBoard;
        private final int boardArea;
        private int blocksPlaced;
        private int currentBuildIndex;
        private Build currentBuild;

        public MatchSectionHandler(MatchPlayer gamePlayer, Region displayBoardRegion, Region placementBoardRegion) {
            this.player = gamePlayer.player();

            Vec topCorner = placementBoardRegion.topCorner();
            Vec bottomCorner = placementBoardRegion.bottomCorner();
            this.boardArea = (topCorner.blockX() - bottomCorner.blockX() + 1) * (topCorner.blockZ() - bottomCorner.blockZ() + 1);

            this.currentBuild = builds[currentBuildIndex];
            this.displayBoard = new Board(displayBoardRegion, currentBuild.blockIds());
            this.placementBoard = new Board(placementBoardRegion, new String[boardArea]);
        }

        private void startBuild() {
            List<ItemStack> blockItems = new ArrayList<>();
            AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
            Vec topCorner = displayBoard.getRegion().topCorner();
            Vec bottomCorner = displayBoard.getRegion().bottomCorner();

            int index = 0;

            for (int z = bottomCorner.blockZ(); z <= topCorner.blockZ(); z++) {
                for (int y = bottomCorner.blockY(); y <= topCorner.blockY(); y++) {
                    String namespaceId = displayBoard.getPlacedBlockIds()[index++];
                    Block block = Block.fromNamespaceId(namespaceId);

                    Check.notNull(block, "Cannot find block " + namespaceId);

                    Material material = Material.fromNamespaceId(namespaceId);

                    Check.notNull(material, "Cannot find material " + namespaceId);

                    batch.setBlock(bottomCorner.blockX(), y, z, block);
                    blockItems.add(ItemStack.of(material));
                }
            }

            batch.apply(player.getInstance(), null);
            player.getInventory().addItemStacks(blockItems, TransactionOption.ALL_OR_NOTHING);
        }

        private void startTasks(TaskManager taskManager) {
//            taskManager.startTask(() -> player.sendActionBar(Component.text("ᴄᴜʀʀᴇɴᴛ ʙᴜɪʟᴅ: ", TextColorPresets.TEXT)
//                    .append(Component.text(currentBuild.displayName(), TextColorPresets.ARGUMENT))), TaskSchedule.immediate(), TaskSchedule.tick(1));
        }

        private EventNode<PlayerEvent> createEventNode() {
            EventNode<PlayerEvent> eventNode = EventNode.type(player.getUsername(), EventFilter.PLAYER, (_, p) -> p.getUuid() == player.getUuid());

            eventNode.addListener(EventListener.builder(PlayerBlockBreakEvent.class)
                    .handler(event -> {
                        if (isNotInPlacementBoard(event.getBlockPosition(), placementBoard)) {
                            event.setCancelled(true);
                            return;
                        }

                        Material blockMaterial = Material.fromNamespaceId(event.getBlock().namespace());

                        if (blockMaterial == null)
                            return;

                        event.getPlayer().getInventory().addItemStack(ItemStack.of(blockMaterial));
                        blocksPlaced--;
                    }).build());
            eventNode.addListener(EventListener.builder(PlayerBlockPlaceEvent.class)
                    .handler(event -> {
                        Point position = event.getBlockPosition();

                        if (isNotInPlacementBoard(position, placementBoard)) {
                            event.setCancelled(true);
                            return;
                        }

                        placementBoard.setPlacedBlockId(translatePositionToIndex(position, placementBoard), event.getBlock().namespace().asString());

                        if (++blocksPlaced == boardArea && Arrays.equals(placementBoard.getPlacedBlockIds(), displayBoard.getPlacedBlockIds())) {
                            MinecraftServer.getSchedulerManager().scheduleNextTick(this::completeBoard);
                        }
                    }).build());

            return eventNode;
        }

        private void completeBoard() {
            clearPlacementBoard();
            player.getInventory().clear();

            game.getScoreKeeper().addScore(player.getUuid(), player, 1, "ʙᴜɪʟᴅ ᴄᴏᴍᴘʟᴇᴛᴇᴅ");

            UUID uuid = player.getUuid();
            MatchPlayer updatedPlayer = game.getPlayerList().updatePlayer(uuid, MatchPlayer::addBuildsCompleted);

            Sidebar sidebar = sidebars.get(player);
            sidebar.updateLineContent("player_stat", Texts.buildsCompleted(updatedPlayer.buildsCompleted()));
            sidebar.updateLineContent("player_score", Texts.score(game.getScoreKeeper().getScore(uuid)));

            if (++currentBuildIndex >= builds.length)
                currentBuildIndex = 0;

            this.currentBuild = builds[currentBuildIndex];
            displayBoard.setPlacedBlockIds(currentBuild.blockIds());
            blocksPlaced = 0;
            startBuild();
        }

        private void clearPlacementBoard() {
            AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
            Vec bottomCorner = placementBoard.getRegion().bottomCorner();
            Vec topCorner = placementBoard.getRegion().topCorner();
            int y = bottomCorner.blockY();

            for (int x = bottomCorner.blockX(); x <= topCorner.blockX(); x++) {
                for (int z = bottomCorner.blockZ(); z <= topCorner.blockZ(); z++) {
                    batch.setBlock(x, y, z, Block.AIR);
                }
            }

            batch.apply(player.getInstance(), null);
            placementBoard.setPlacedBlockIds(new String[boardArea]);
        }

        private static boolean isNotInPlacementBoard(Point point, Board placementBoard) {
            Vec bottomCorner = placementBoard.getRegion().bottomCorner();
            Vec topCorner = placementBoard.getRegion().topCorner();
            return !((point.x() >= bottomCorner.x() && point.x() <= topCorner.x())
                    && (point.y() >= bottomCorner.y() && point.y() <= topCorner.y())
                    && (point.z() >= bottomCorner.z() && point.z() <= topCorner.z()));
        }

        private static int translatePositionToIndex(Point point, Board board) {
            Vec bottomCorner = board.getRegion().bottomCorner();
            Vec topCorner = board.getRegion().topCorner();
            int rows = topCorner.blockX() - bottomCorner.blockX() + 1; // 8
            int area = (point.blockZ() - bottomCorner.blockZ()) * rows;

            int column = point.blockX() - bottomCorner.blockX();

            return area + column;
        }

    }
}
