package dev.zenqrt.mso.lobby.rainbowman;

import dev.zenqrt.mso.lobby.data.configuration.RainbowManConfig;
import dev.zenqrt.mso.lobby.entity.NPC;
import dev.zenqrt.mso.lobby.entity.NPCLookAt;
import dev.zenqrt.mso.lobby.item.ItemRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.animal.SheepMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEntityInteractEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;

// TODO: Add dialogue
public final class RainbowManHandler {

    private final RainbowManConfig config;

    public RainbowManHandler(RainbowManConfig config) {
        this.config = config;
    }

    public void init(Instance instance) {
        NPC npc = new NPCLookAt("rainbowman", PlayerSkin.fromUsername("Minikloon"));
        npc.setInstance(instance, config.npcPosition());
        npc.hideNameTag();

        Entity npcTag = new Entity(EntityType.TEXT_DISPLAY);
        npcTag.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setHasNoGravity(true);
        });
        MinecraftServer.getSchedulerManager().scheduleTask(new RainbowTagTask(npcTag, "<b>Minikloon"), TaskSchedule.immediate(), TaskSchedule.tick(2));
        npcTag.setInstance(instance, npc.getPosition().add(0, 2.15, 0));

        Entity sheep = new Entity(EntityType.SHEEP);
        sheep.editEntityMeta(SheepMeta.class, meta -> {
            meta.setCustomName(Component.text("jeb_"));
            meta.setCustomNameVisible(false);
        });

        sheep.setInstance(instance, config.sheepPosition());

        MinecraftServer.getGlobalEventHandler().addListener(EventListener.builder(PlayerEntityInteractEvent.class)
                .filter(event -> event.getTarget().equals(npc))
                .handler(event -> event.getPlayer().getInventory().addItemStack(ItemRegistry.RAINBOW_SHEEPINATOR.buildItemStack()))
                .build());
    }

    private static class RainbowTagTask implements Runnable {

        private final Entity tagEntity;
        private final String text;
        private double phase = -1;

        RainbowTagTask(Entity tagEntity, String text) {
            this.tagEntity = tagEntity;
            this.text = text;
        }

        @Override
        public void run() {
            tagEntity.editEntityMeta(TextDisplayMeta.class, meta ->
                    meta.setText(MiniMessage.miniMessage().deserialize("<gradient:#ff70f8:#e0b1de:%.2f".formatted(phase) + ">" + text)));

            phase += 0.05;
            if (phase >= 1) {
                phase = -1;
            }
        }
    }
}
