package dev.zenqrt.mso.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.anvil.AnvilLoader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public final class MinestomGameServer {

    private static final Gson GSON = new Gson();
    private final MinecraftServer server;
    private final Instance instance;
    private final JsonObject configJson;

    public MinestomGameServer(MinecraftServer server, Instance instance, JsonObject configJson) {
        this.server = server;
        this.instance = instance;
        this.configJson = configJson;
    }

    public void start(int port) {
        server.start("127.0.0.1", port);
    }

    public Instance getInstance() {
        return instance;
    }

    public JsonObject getConfigJson() {
        return configJson;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static MinestomGameServer init() throws URISyntaxException, IOException {
        MinecraftServer server = MinecraftServer.init();
        MojangAuth.init();

        URL worldUrl = Objects.requireNonNull(MinestomGameServer.class.getClassLoader().getResource("map/world"), "world folder");
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(new AnvilLoader(Path.of(worldUrl.toURI())));
        instance.setGenerator(_ -> {});

        JsonObject configJson;

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MinestomGameServer.class.getClassLoader().getResourceAsStream("map/config.json")))) {
            configJson = GSON.fromJson(reader, JsonObject.class);
        }

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);

            Player player = event.getPlayer();

            JsonObject spawnJson = configJson.getAsJsonObject("spawn");
            double x = spawnJson.get("x").getAsDouble();
            double y = spawnJson.get("y").getAsDouble();
            double z = spawnJson.get("z").getAsDouble();

            player.setRespawnPoint(new Pos(x, y, z));
            player.setGameMode(GameMode.ADVENTURE);
        });
        return new MinestomGameServer(server, instance, configJson);
    }
}
