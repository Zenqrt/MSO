package dev.zenqrt.mso.parkourrace.game.states;

import dev.zenqrt.mso.game.state.GameState;
import map.ParkourRaceConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;

public final class DisplayCheckpointsGameState extends GameState {

    private final Instance instance;
    private final ParkourRaceConfig config;

    public DisplayCheckpointsGameState(Instance instance, ParkourRaceConfig config) {
        this.instance = instance;
        this.config = config;
    }

    @Override
    protected void onStateStart() {
        Pos[] checkpoints = config.checkpoints();

        for (int i = 0; i < checkpoints.length; i++) {
            Pos checkpoint = checkpoints[i];

            Entity textDisplay = new Entity(EntityType.TEXT_DISPLAY);
            Component text = Component.text("Checkpoint #" + (i + 1), NamedTextColor.GREEN);
            textDisplay.editEntityMeta(TextDisplayMeta.class, meta -> {
                meta.setText(text);
                meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.CENTER);
                meta.setHasNoGravity(true);
            });
            textDisplay.setInstance(instance, checkpoint.add(0.5, 2, 0.5));
        }
    }
}
