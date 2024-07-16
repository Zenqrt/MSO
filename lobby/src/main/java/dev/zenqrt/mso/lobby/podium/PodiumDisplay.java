package dev.zenqrt.mso.lobby.podium;

import dev.zenqrt.mso.lobby.entity.podium.PodiumNPC;
import dev.zenqrt.mso.lobby.entity.podium.PodiumNPCTag;
import dev.zenqrt.mso.text.Icons;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.entity.metadata.EntityMeta;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;

public final class PodiumDisplay {

    private final PodiumNPC npc;
    private final Entity textDisplay;
    private final PodiumNPCTag npcTag;
    private final Entity scoreTextDisplay;

    public PodiumDisplay(PodiumNPC npc, Entity textDisplay) {
        this.npc = npc;
        this.textDisplay = textDisplay;
        this.npcTag = new PodiumNPCTag(npc.getUsername());
        this.scoreTextDisplay = new Entity(EntityType.TEXT_DISPLAY);
        this.scoreTextDisplay.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(scoreText(0));
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
            meta.setHasNoGravity(true);
        });
    }

    public void show(Instance instance, Point npcPosition, Point textDisplayPosition) {
        npc.setInstance(instance, npcPosition)
                .thenRun(() -> {
                    Entity tagHider = new Entity(EntityType.SLIME);
                    tagHider.editEntityMeta(EntityMeta.class, meta -> meta.setInvisible(true));
                    tagHider.setInstance(instance).thenRun(() -> npc.addPassenger(tagHider));
                });
        npcTag.setInstance(instance, npcPosition.add(0, npc.getEyeHeight() + 1.35, 0));
        textDisplay.setInstance(instance, textDisplayPosition);
        scoreTextDisplay.setInstance(instance, npcPosition.add(0, 3.3, 0));
    }

    public void update(String username, PlayerSkin skin, int score) {
        npc.setUsername(username);
        npc.setSkin(skin);

        npcTag.updateUsername(username);
        scoreTextDisplay.editEntityMeta(TextDisplayMeta.class, meta -> meta.setText(scoreText(score)));
    }

    private static Component scoreText(int score) {
        return Component.text(score, NamedTextColor.WHITE)
                .append(Component.space())
                .append(Icons.SCORE);
    }
}
