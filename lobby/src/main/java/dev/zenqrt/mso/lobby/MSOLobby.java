package dev.zenqrt.mso.lobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.zenqrt.mso.lobby.block.handler.PlayerHeadBlockHandler;
import dev.zenqrt.mso.lobby.configuration.MSOLobbyConfig;
import dev.zenqrt.mso.lobby.gson.deserializers.PosDeserializer;
import dev.zenqrt.mso.lobby.gson.deserializers.VecDeserializer;
import dev.zenqrt.mso.lobby.item.ItemRegistry;
import dev.zenqrt.mso.lobby.rainbowman.RainbowManHandler;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
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

public final class MSOLobby {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Pos.class, new PosDeserializer())
            .registerTypeAdapter(Vec.class, new VecDeserializer())
            .create();

    @SuppressWarnings("UnstableApiUsage")
    public static void main(String[] args) throws URISyntaxException, IOException {
        MinecraftServer server = MinecraftServer.init();
        VelocityProxy.enable(readForwardingSecret());

        URL worldUrl = Objects.requireNonNull(MSOLobby.class.getClassLoader().getResource("map/world"), "world folder");
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer(new AnvilLoader(Path.of(worldUrl.toURI())));
        instance.setGenerator(_ -> {});

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);

            Player player = event.getPlayer();
            player.setRespawnPoint(new Pos(0.5, 101, 0.5));
            player.setGameMode(GameMode.ADVENTURE);
        });
        MinecraftServer.getGlobalEventHandler().addListener(EventListener.builder(PlayerMoveEvent.class)
                .filter(event -> event.getNewPosition().y() <= 0)
                .handler(event -> event.getPlayer().teleport(event.getPlayer().getRespawnPoint()))
                .build());

        MinecraftServer.getBlockManager().registerHandler("minecraft:skull", PlayerHeadBlockHandler::new);
        ItemRegistry.registerItemEvents();

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MSOLobby.class.getClassLoader().getResourceAsStream("map/config.json")))) {
            MSOLobbyConfig config = GSON.fromJson(reader, MSOLobbyConfig.class);
            RainbowManHandler rainbowManHandler = new RainbowManHandler(config.rainbowManSettings());
            rainbowManHandler.init(instance);
        }

        server.start("127.0.0.1", 25565);
    }

    private static String readForwardingSecret() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(MSOLobby.class.getClassLoader().getResourceAsStream("forwarding.secret"),
                        "forwarding secret stream"), StandardCharsets.UTF_8))) {
            return reader.readLine();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
