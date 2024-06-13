package dev.zenqrt.mso.tntrun;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.anvil.AnvilLoader;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public final class TNTRun {

    @SuppressWarnings("UnstableApiUsage")
    public static void main(String[] args) throws URISyntaxException {
        MinecraftServer server = MinecraftServer.init();
        MojangAuth.init();

        URL worldUrl = Objects.requireNonNull(TNTRun.class.getClassLoader().getResource("map/world"), "world folder");
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(new AnvilLoader(Path.of(worldUrl.toURI())));
        instance.setGenerator(_ -> {});

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);

            Player player = event.getPlayer();
            player.setRespawnPoint(new Pos(0.5, 101, 0.5));
            player.setGameMode(GameMode.ADVENTURE);
        });

        server.start("127.0.0.1", 25566);
    }

}
