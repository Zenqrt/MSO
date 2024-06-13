package dev.zenqrt.mso.tntrun.game.state.states;

import dev.zenqrt.mso.tntrun.game.TNTRunGame;
import dev.zenqrt.mso.tntrun.game.state.EventGameState;
import dev.zenqrt.mso.tntrun.map.TNTRunConfig;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;
import java.util.List;

public final class ActiveGameState extends EventGameState {

    private final TNTRunGame game;
    private final TNTRunConfig config;
    private final List<Player> playersLeft;
    private final Player[] topPlayers;

    public ActiveGameState(EventNode<Event> parentNode, TNTRunGame game, TNTRunConfig config) {
        super(parentNode);

        this.game = game;
        this.config = config;
        this.playersLeft = new ArrayList<>(game.getInstance().getPlayers());
        this.topPlayers = new Player[3];
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> event.getNewPosition().y() < config.bottomYLevel())
                .handler(event -> {
                    Player player = event.getPlayer();

                    player.setGameMode(GameMode.SPECTATOR);
                    player.setInvisible(true);
                    playersLeft.remove(player);

                    // TODO: Add score here

                    if (playersLeft.size() < 3) {
                        topPlayers[playersLeft.size()] = player;

                        if (playersLeft.size() == 1) {
                            topPlayers[0] = playersLeft.stream().findFirst().orElseThrow();
                            // TODO: Switch state here
                        }
                    }

                    // TODO: Send death title here
                }).build());
    }

    private static class BlockRemoveTask implements Runnable {

        private final Player player;
        private Point lastBlockPosition;
        private long lastChangedBlockTime;

        BlockRemoveTask(Player player) {
            this.player = player;
            this.lastBlockPosition = player.getPosition().asVec().apply(Vec.Operator.FLOOR);
            this.lastChangedBlockTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            Point position = player.getPosition().asVec().apply(Vec.Operator.FLOOR);

            if (position.samePoint(lastBlockPosition) || currentTime - lastChangedBlockTime < 500) {
                return;
            }

            Instance instance = player.getInstance();
            Point tntPosition = position.sub(0, 2, 0);
            Block expectedTntBlock = instance.getBlock(tntPosition);

            if (!expectedTntBlock.compare(Block.TNT))
                return;

            instance.setBlock(position.sub(0, 1, 0), Block.AIR);
            instance.setBlock(tntPosition, Block.AIR);

            lastBlockPosition = position;
            lastChangedBlockTime = currentTime;
        }
    }
}
