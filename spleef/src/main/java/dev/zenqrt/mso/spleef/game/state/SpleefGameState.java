package dev.zenqrt.mso.spleef.game.state;

import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.game.text.GameMessages;
import dev.zenqrt.mso.game.text.Titles;
import dev.zenqrt.mso.spleef.game.map.SpleefConfig;
import dev.zenqrt.mso.spleef.game.player.SpleefPlayer;
import io.github.togar2.pvp.feature.CombatFeatures;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.EntityInstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

public final class SpleefGameState extends EventGameState {

    private final Instance instance;
    private final GamePlayerList<SpleefPlayer> playerList;
    private final SpleefConfig config;
    private final ScoreKeeper scoreKeeper;
    private final List<Player> playersLeft = new ArrayList<>();
    private final List<GamePlayer> topPlayers = new ArrayList<>(3);
    private final EventNode<EntityInstanceEvent> combatEventNode;

    public SpleefGameState(EventNode<Event> parentNode, Instance instance, GamePlayerList<SpleefPlayer> playerList, SpleefConfig config, ScoreKeeper scoreKeeper) {
        super(parentNode);

        this.instance = instance;
        this.playerList = playerList;
        this.config = config;
        this.scoreKeeper = scoreKeeper;
        this.combatEventNode = CombatFeatures.empty()
                .add(CombatFeatures.VANILLA_MISC_PROJECTILE)
                .build().createNode();
    }

    @Override
    protected void registerEvents() {
        eventNode.addListener(ItemDropEvent.class, event -> event.setCancelled(true));
        eventNode.addListener(EventListener.builder(PlayerStartDiggingEvent.class)
                .filter(event -> event.getPlayer().getItemInMainHand().material() == Material.WOODEN_SHOVEL)
                .handler(event -> event.getInstance().breakBlock(event.getPlayer(), event.getBlockPosition(), BlockFace.BOTTOM))
                .build());
        eventNode.addListener(PlayerBlockBreakEvent.class, event -> {
            if (!event.getBlock().compare(Block.SNOW_BLOCK)) {
                event.setCancelled(true);
                return;
            }

            event.getPlayer().getInventory().addItemStack(ItemStack.of(Material.SNOWBALL));
        });
        eventNode.addListener(EventListener.builder(ProjectileCollideWithBlockEvent.class)
                .filter(event -> {
                    System.out.println("Collision");
                    return event.getBlock().compare(Block.SNOW_BLOCK);
                })
                .handler(event -> event.getInstance().setBlock(event.getCollisionPosition(), Block.AIR))
                .build());
        eventNode.addListener(EventListener.builder(PlayerUseItemEvent.class)
                .filter(event -> event.getItemStack().material() == Material.SNOWBALL)
                .handler(event -> event.setItemUseTime(5)).build());
       eventNode.addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> playersLeft.contains(event.getPlayer()))
                .filter(event -> event.getNewPosition().y() < config.bottomYLevel())
                .handler(event -> {
                    Player player = event.getPlayer();

                    eliminatePlayer(player);

                    topPlayers.addFirst(playerList.getPlayer(player.getUuid()));
                    playersLeft.forEach(playerLeft -> scoreKeeper.addScore(playerLeft.getUuid(), playerLeft, 2, "Survival"));

                    if (playersLeft.size() == 1) {
                        topPlayers.addFirst(playerList.getPlayer(playersLeft.getFirst().getUuid()));
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

        Audiences.players().sendMessage(GameMessages.death(player.getUsername(), "{username} has been eliminated!"));
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();

        instance.eventNode().addChild(combatEventNode);
        playerList.forEach(gamePlayer -> {
            Player player = gamePlayer.player();
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().addItemStack(
                    ItemStack.builder(Material.WOODEN_SHOVEL)
                            .set(ItemComponent.ITEM_NAME, Component.text("Spleefinator", NamedTextColor.AQUA))
                            .build()
            );
            playersLeft.add(player);
        });
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();
        instance.eventNode().removeChild(combatEventNode);
    }
}
