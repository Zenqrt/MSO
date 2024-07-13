package dev.zenqrt.mso.match.game.states;

import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.match.game.MatchGame;
import dev.zenqrt.mso.match.game.board.Board;
import dev.zenqrt.mso.match.game.map.MatchSectionArea;
import dev.zenqrt.mso.match.utils.coordinate.Region;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.batch.AbsoluteBlockBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

public final class BuildMatchingGameState extends EventGameState {

    private final MatchGame game;
    private final String[][] builds;

    public BuildMatchingGameState(MatchGame game, String[][] builds) {
        super(game.getEventNode());

        this.game = game;
        this.builds = builds;
    }

    @Override
    protected void registerEvents() {
        game.getPlayerList().forEach(gamePlayer -> {
            Player player = gamePlayer.player();
            MatchSectionArea matchSection = game.getPlayerSections().get(player.getUuid());

            MatchSectionHandler handler = new MatchSectionHandler(player, matchSection.displayBoard(), matchSection.placementBoard());
            eventNode.addChild(handler.createEventNode());
        });
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        game.getPlayerList().forEach(gamePlayer -> gamePlayer.player().setInstantBreak(true));
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();
        game.getPlayerList().forEach(gamePlayer -> gamePlayer.player().setInstantBreak(false));
    }

    private class MatchSectionHandler {

        private final Player player;
        private final Board displayBoard;
        private final Board placementBoard;
        private final int boardArea;
        private int blocksPlaced;
        private int currentBuildIndex;

        public MatchSectionHandler(Player player, Region displayBoardRegion, Region placementBoardRegion) {
            this.player = player;

            Vec topCorner = placementBoardRegion.topCorner();
            Vec bottomCorner = placementBoardRegion.bottomCorner();
            this.boardArea = (topCorner.blockX() - bottomCorner.blockX()) * (topCorner.blockZ() - bottomCorner.blockZ());

            this.displayBoard = new Board(displayBoardRegion, builds[currentBuildIndex]);
            this.placementBoard = new Board(placementBoardRegion, new String[boardArea]);
        }

        private EventNode<PlayerEvent> createEventNode() {
            EventNode<PlayerEvent> eventNode = EventNode.type(player.getUsername(), EventFilter.PLAYER, (_, p) -> p.getUuid() == player.getUuid());

            eventNode.addListener(EventListener.builder(PlayerBlockBreakEvent.class)
                    .handler(event -> {
                        if (!isNotInPlacementBoard(event.getBlockPosition(), placementBoard)) {
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

                        if (++blocksPlaced == boardArea && placementBoard.getPlacedBlockIds() == displayBoard.getPlacedBlockIds()) {
                            clearPlacementBoard();
                            blocksPlaced = 0;
                            displayBoard.setPlacedBlockIds(builds[++currentBuildIndex]);
                            game.getScoreKeeper().addScore(player.getUuid(), player, 1, "Build completed");
                        }
                    }).build());

            return eventNode;
        }

        private void clearPlacementBoard() {
            AbsoluteBlockBatch batch = new AbsoluteBlockBatch();
            Vec bottomCorner = placementBoard.getRegion().bottomCorner();
            Vec topCorner = placementBoard.getRegion().topCorner();
            int y = bottomCorner.blockY();

            for (int x = bottomCorner.blockX(); x < topCorner.blockX(); x++) {
                for (int z = bottomCorner.blockZ(); z < topCorner.blockZ(); z++) {
                    batch.setBlock(x, y, z, Block.AIR);
                }
            }

            batch.apply(player.getInstance(), null);
            placementBoard.setPlacedBlockIds(new String[]{});
        }

        private static boolean isNotInPlacementBoard(Point point, Board placementBoard) {
            Vec bottomCorner = placementBoard.getRegion().bottomCorner();
            Vec topCorner = placementBoard.getRegion().topCorner();
            return !(point.x() >= bottomCorner.x() && point.x() <= topCorner.x())
                    && (point.z() >= bottomCorner.z() && point.z() <= topCorner.z());
        }

        private static int translatePositionToIndex(Point point, Board board) {
            Vec bottomCorner = board.getRegion().bottomCorner();
            Vec topCorner = board.getRegion().topCorner();
            int columns = topCorner.blockZ() - bottomCorner.blockZ();
            int area = (point.blockX() - bottomCorner.blockX() - 1) * columns;

            int column = point.blockZ() - bottomCorner.blockZ();

            return area + column;
        }

    }
}
