package dev.zenqrt.mso.oitc.game.states;

import dev.zenqrt.mso.game.player.GamePlayerList;
import dev.zenqrt.mso.game.score.ScoreKeeper;
import dev.zenqrt.mso.game.sidebar.GameSidebar;
import dev.zenqrt.mso.game.state.EventGameState;
import dev.zenqrt.mso.game.task.TaskManager;
import dev.zenqrt.mso.oitc.game.map.OneInTheChamberConfig;
import dev.zenqrt.mso.oitc.game.player.OneInTheChamberPlayer;
import dev.zenqrt.mso.text.TextColorPresets;
import io.github.togar2.pvp.entity.projectile.Arrow;
import io.github.togar2.pvp.events.EntityPreDeathEvent;
import io.github.togar2.pvp.events.FinalDamageEvent;
import io.github.togar2.pvp.events.PickupEntityEvent;
import io.github.togar2.pvp.feature.CombatFeatures;
import io.github.togar2.pvp.utils.CombatVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.entity.projectile.ProjectileCollideWithBlockEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.EntityInstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.world.Difficulty;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public final class BattleGameState extends EventGameState {

    private final Instance instance;
    private final GamePlayerList<OneInTheChamberPlayer> playerList;
    private final OneInTheChamberConfig config;
    private final ScoreKeeper scoreKeeper;
    private final Map<Player, GameSidebar> sidebars;
    private final TaskManager taskManager;
    private final EventNode<EntityInstanceEvent> combatEventNode;

    public BattleGameState(EventNode<Event> parentNode, Instance instance, GamePlayerList<OneInTheChamberPlayer> playerList, OneInTheChamberConfig config, ScoreKeeper scoreKeeper, Map<Player, GameSidebar> sidebars) {
        super(parentNode);

        this.instance = instance;
        this.playerList = playerList;
        this.config = config;
        this.scoreKeeper = scoreKeeper;
        this.sidebars = sidebars;
        this.taskManager = new TaskManager(MinecraftServer.getSchedulerManager());
        this.combatEventNode = CombatFeatures.empty().difficulty(_ -> Difficulty.HARD).version(CombatVersion.MODERN)
                .add(CombatFeatures.VANILLA_ATTACK)
                .add(CombatFeatures.VANILLA_ATTACK_COOLDOWN)
                .add(CombatFeatures.VANILLA_CRITICAL)
                .add(CombatFeatures.VANILLA_DAMAGE)
                .add(CombatFeatures.VANILLA_KNOCKBACK)
                .add(CombatFeatures.VANILLA_SWEEPING)
                .add(CombatFeatures.VANILLA_PROJECTILE_ITEM)
                .add(CombatFeatures.VANILLA_BOW)
                .build().createNode();
    }

    @Override
    protected void registerEvents() {
        combatEventNode.addListener(ProjectileCollideWithBlockEvent.class, event -> {
            event.setCancelled(true);
            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> event.getEntity().remove());
        });
        combatEventNode.addListener(PickupEntityEvent.class, event -> event.setCancelled(true));
        eventNode.addListener(InventoryPreClickEvent.class, event -> event.setCancelled(true));
        eventNode.addListener(EventListener.builder(EntityDamageEvent.class)
                .handler(event -> {
                    Damage damage = event.getDamage();

                    if (!(event.getEntity() instanceof Player player && damage.getSource() instanceof Arrow arrow))
                        return;

                    Entity shooter = damage.getAttacker();

                    if (shooter == player) {
                        event.setCancelled(true);
                        arrow.remove();
                        return;
                    }

                    player.damage(Damage.fromProjectile(shooter, arrow, 1000));
                }).build());
        eventNode.addListener(EventListener.builder(FinalDamageEvent.class)
                .filter(FinalDamageEvent::doesKillEntity)
                .handler(event -> {
                    if (!(event.getEntity() instanceof Player target))
                        return;

                    Damage damage = event.getDamage();

                    if (!(damage.getAttacker() instanceof Player attacker))
                        return;

                    target.setGameMode(GameMode.SPECTATOR);
                    target.setInvisible(true);
                    target.spectate(attacker);

                    new KillCamTask(5, target, attacker).start();

                    PlayerInventory inventory = attacker.getInventory();

                    if (inventory.getItemStack(8).material() != Material.ARROW)
                        inventory.setItemStack(8, ItemStack.of(Material.ARROW));
                    else
                        inventory.addItemStack(ItemStack.of(Material.ARROW));

                    UUID attackerUuid = attacker.getUuid();
                    String symbol = damage.getSource() instanceof Arrow ? "\uD83C\uDFF9" : "\uD83D\uDDE1";
                    playerList.getPlayersAsAudience().sendMessage(Component.text("{killer} " + symbol + " {killed}", NamedTextColor.RED)
                            .replaceText(builder ->
                                    builder.matchLiteral("{killer}")
                                            .replacement(attacker.getUsername()))
                            .replaceText(builder ->
                                    builder.matchLiteral("{killed}")
                                            .replacement(target.getUsername())));
                                playerList.updatePlayer(attackerUuid, OneInTheChamberPlayer::addKill);

                    scoreKeeper.addScore(attackerUuid, attacker, 1, "Kill");
                    sidebars.get(attacker).updateScore(scoreKeeper.getScore(attackerUuid));
                    sidebars.forEach((_, sidebar) -> sidebar.updateLeaderboard(playerList, scoreKeeper));

                }).build());
        eventNode.addListener(EntityPreDeathEvent.class, event -> {
            if (!(event.getEntity() instanceof Player))
                return;

            event.setCancelDeath(true);
        });
    }

    private void respawn(Player player) {
        player.setHealth(20);
        player.setArrowCount(0);
        giveItems(player.getInventory());

        player.teleport(chooseBestSpawn());
        player.setGameMode(GameMode.ADVENTURE);
        player.setInvisible(false);
    }

    private Pos chooseBestSpawn() {
        return Arrays.stream(config.spawnPositions())
                .max(Comparator.comparingDouble(this::closestDistanceToPlayer))
                .orElseThrow()
                .asPosition();
    }

    private double closestDistanceToPlayer(Point position) {
        return playerList.getPlayers().values().stream()
                .mapToDouble(gamePlayer -> position.distance(gamePlayer.player().getPosition()))
                .min()
                .orElse(0);
    }

    private static void giveItems(PlayerInventory inventory) {
        inventory.setItemStack(0, ItemStack.of(Material.STONE_SWORD));
        inventory.setItemStack(1, ItemStack.of(Material.BOW));
        inventory.setItemStack(8, ItemStack.of(Material.ARROW));
    }

    @Override
    protected void onStateStart() {
        super.onStateStart();
        instance.eventNode().addChild(combatEventNode);
        playerList.forEach(gamePlayer -> giveItems(gamePlayer.player().getInventory()));
    }

    @Override
    protected void onStateEnd() {
        super.onStateEnd();
        instance.eventNode().removeChild(combatEventNode);
    }

    private class KillCamTask implements Runnable {

        private final Player killed;
        private final String killerUsername;
        private int timeLeft;
        private Task task;

        KillCamTask(int time, Player killed, Player killer) {
            this.timeLeft = time;
            this.killed = killed;
            this.killerUsername = killer.getUsername();
        }

        @Override
        public void run() {
            if (timeLeft <= 0) {
                taskManager.stopTask(task);

                killed.stopSpectating();
                killed.clearTitle();
                killed.sendActionBar(Component.empty());
                respawn(killed);
                return;
            }

            killed.showTitle(Title.title(
                    Component.text("ᴋɪʟʟᴇᴅ ʙʏ", NamedTextColor.GRAY),
                    Component.text(killerUsername, NamedTextColor.WHITE),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(10), Duration.ZERO)));
            killed.sendActionBar(Component.text("ʀᴇsᴘᴀᴡɴɪɴɢ ɪɴ {seconds}...", TextColorPresets.TEXT)
                    .replaceText(builder ->
                            builder.matchLiteral("{seconds}")
                                    .replacement(Component.text(timeLeft + "s", TextColorPresets.ARGUMENT))));
            timeLeft--;
        }

        void start() {
            task = taskManager.startTask(this, TaskSchedule.immediate(), TaskSchedule.seconds(1));
        }
    }
}
