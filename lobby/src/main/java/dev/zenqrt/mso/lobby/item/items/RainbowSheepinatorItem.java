package dev.zenqrt.mso.lobby.item.items;

import dev.zenqrt.mso.lobby.item.CustomItem;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

// TODO: Add cooldowns
// TODO: Add rainbow particles
public final class RainbowSheepinatorItem extends CustomItem {

    public RainbowSheepinatorItem() {
        super("rainbow_sheepinator", Material.WHEAT,
                MiniMessage.miniMessage().deserialize("<gradient:red:gold:yellow:green:aqua:blue:light_purple><b>Rainbowfied Sheepinator 3000"));
    }

    @Override
    protected void registerEvents() {
        playerEventNode.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            Vec velocity = player.getPosition().direction().normalize().mul(20).add(0, 10, 0);

            LivingEntity sheep = new LivingEntity(EntityType.SHEEP);
            sheep.editEntityMeta(EntityMeta.class, meta -> {
                meta.setCustomName(Component.text("jeb_"));
                meta.setCustomNameVisible(false);
            });
            sheep.setInstance(event.getInstance(), player.getPosition().add(0, player.getEyeHeight(), 0)).thenRun(() -> sheep.setVelocity(velocity));

            player.playSound(Sound.sound(SoundEvent.ENTITY_SHEEP_AMBIENT, Sound.Source.NEUTRAL, 1, 1.25F), Sound.Emitter.self());
            sheep.scheduler().scheduleTask(() -> {
                if (sheep.getPosition().y() <= 0) {
                    sheep.remove();
                    return;
                }

                if (sheep.isOnGround()) {
                    ParticlePacket particlePacket = new ParticlePacket(
                            Particle.EXPLOSION,
                            sheep.getPosition(),
                            Vec.ZERO,
                            0,
                            3
                    );
                    sheep.sendPacketsToViewers(particlePacket);
                    sheep.getViewersAsAudience().playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.MASTER, 0.75F, 0));
                    sheep.remove();
                }
            }, TaskSchedule.immediate(), TaskSchedule.tick(1));
        });
    }
}
