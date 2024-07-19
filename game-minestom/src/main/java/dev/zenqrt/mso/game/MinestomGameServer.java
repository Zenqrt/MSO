package dev.zenqrt.mso.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.commands.TeleportCommand;
import dev.zenqrt.mso.game.permission.Permissions;
import dev.zenqrt.mso.game.player.GamePlayerProvider;
import dev.zenqrt.mso.game.player.MinestomGamePlayer;
import dev.zenqrt.mso.player.Players;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.anvil.AnvilLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

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

    public static <T extends MinestomGamePlayer> Builder<T> builder(Class<T> ignored) {
        return new Builder<>();
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
    private static <T extends MinestomGamePlayer> MinestomGameServer init(GamePlayerProvider<T, Player> gamePlayerProvider, Function<MinestomGameServer, MinestomGame<T>> gameSupplier) throws URISyntaxException, IOException {
        MinecraftServer server = MinecraftServer.init();
        VelocityProxy.enable(readForwardingSecret());

        URL worldUrl = Objects.requireNonNull(MinestomGameServer.class.getClassLoader().getResource("map/world"), "world folder");
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(new AnvilLoader(Path.of(worldUrl.toURI())));
        instance.setGenerator(_ -> {});

        JsonObject configJson;

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MinestomGameServer.class.getClassLoader().getResourceAsStream("map/config.json")))) {
            configJson = GSON.fromJson(reader, JsonObject.class);
        }

        MinecraftServer.getCommandManager().register(new TeleportCommand());

        MinestomGameServer minestomGameServer = new MinestomGameServer(server, instance, configJson);
        MinestomGame<T> game = gameSupplier.apply(minestomGameServer);

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> event.setSpawningInstance(instance));
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();

            if (Players.isExcluded(player.getUsername())) {
                player.setGameMode(GameMode.SPECTATOR);
                player.setInvisible(true);
                player.addPermission(Permissions.ADMIN);
                return;
            }
            game.getPlayerList().addPlayer(gamePlayerProvider.createPlayer(player.getUuid(), player, 0));
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerDisconnectEvent.class, event ->
                game.getPlayerList().removePlayer(event.getPlayer().getUuid()));

        game.start();

        return minestomGameServer;
    }

    private static String readForwardingSecret() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(MinestomGameServer.class.getClassLoader().getResourceAsStream("forwarding.secret"),
                        "forwarding secret stream"), StandardCharsets.UTF_8))) {
            return reader.readLine();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static class Builder<T extends MinestomGamePlayer> {

        private GamePlayerProvider<T, Player> gamePlayerProvider;
        private Function<MinestomGameServer, MinestomGame<T>> gameSupplier;

        Builder() {}

        public Builder<T> gamePlayerProvider(GamePlayerProvider<T, Player> gamePlayerProvider) {
            this.gamePlayerProvider = gamePlayerProvider;
            return this;
        }

        public Builder<T> gameSupplier(Function<MinestomGameServer, MinestomGame<T>> gameSupplier) {
            this.gameSupplier = gameSupplier;
            return this;
        }

        public MinestomGameServer build() {
            try {
                return MinestomGameServer.init(gamePlayerProvider, gameSupplier);
            } catch (URISyntaxException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void start(int port) {
            build().start(port);
        }

    }

}
