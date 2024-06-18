package dev.zenqrt.mso.lobby;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.zenqrt.mso.lobby.block.handler.PlayerHeadBlockHandler;
import dev.zenqrt.mso.lobby.configuration.MSOLobbyConfig;
import dev.zenqrt.mso.lobby.configuration.PodiumPlacementConfig;
import dev.zenqrt.mso.lobby.entity.NPC;
import dev.zenqrt.mso.lobby.gson.deserializers.PosDeserializer;
import dev.zenqrt.mso.lobby.gson.deserializers.VecDeserializer;
import dev.zenqrt.mso.lobby.item.ItemRegistry;
import dev.zenqrt.mso.lobby.podium.PodiumHandler;
import dev.zenqrt.mso.lobby.rainbowman.RainbowManHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
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
import java.util.List;
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

            List<PodiumPlacementConfig> podiumPlacementConfigs = config.podiumPlacements();
            PodiumHandler podiumHandler = new PodiumHandler(new PodiumDisplay[]{
                    createPodiumDisplay(instance, podiumPlacementConfigs.getFirst(), Component.text("1st Place", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)),
                    createPodiumDisplay(instance, podiumPlacementConfigs.get(1), Component.text("2nd Place", NamedTextColor.GRAY).decorate(TextDecoration.BOLD)),
                    createPodiumDisplay(instance, podiumPlacementConfigs.get(2), Component.text("3rd Place", TextColor.color(0xB87333)).decorate(TextDecoration.BOLD))
            });
            podiumHandler.init();
            MinecraftServer.getGlobalEventHandler().addChild(podiumHandler.getEventNode());
        }

        server.start("127.0.0.1", 25565);
    }

    private static PodiumDisplay createPodiumDisplay(Instance instance, PodiumPlacementConfig config, Component labelText) {
        NPC npc = new NPC("???", new PlayerSkin(
                "ewogICJ0aW1lc3RhbXAiIDogMTcxODcyNzUxMzgwMCwKICAicHJvZmlsZUlkIiA6ICIwMGI4MDlmY2JlZDQ0YWE3OTcyZWVkOGExY2MyMDg4MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJBcmllbF9wb29wIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzNjZDhmMDI1MDE3ODVkMTZiMDkzZjRkNjU5Njk4YTc5ZmNkNTBhMDA5NGY1MzM0OWQ0OGQ2NjMyZmM2ZTEzMmQiCiAgICB9CiAgfQp9",
                "oAgvlzDKK8Z9SH51O9N0Tv1L5gml0svg/gGOwgmvivf+hqcO9B3c6tZhZP7uIoOdNl9TVuIA66/B+mTwIG+XqJpJCp90ImkpJmFXEqZ8gdvk+Oi9d97XLmGnMoh+2x93yLXHut69hpO50GTzDy2TQXha54aZsdG5yKrEEkGQdySiVGpzMdGGNsVqGlEFtsYRR0zuB1POgXge2csjQw3vlw0ClJn6fysRgU2ItVUrEICh6CXFt86WZ3bX3m/1NwBHghvFQYEu2Lvs9lHjXduNyPPXnPCZ99v0AOna4t+omYyBZakXqCEKSuYjN+9vd4xMnNtYZ+tXdbD4zo0tHU0++DIhQZfvzA8uxunftilSCqaqGkwhSW9gYSzd7NdOodQWXarSzlVqUGg2Ond8rgn4zA1W706TUKGpwRkxclLsD59+701NLVvzxhCPgu2lkCsLabE1Lm7iJEp7lRjeumu8bBmyCiAtKV6t6gB6Bp7FANzYvq9UFi689zZ093afs+z5p+MUUCm0c4BjaaAtAXfLtvBmGAp6XocI0fReCiJb6FiHmNGhkz0seyuV3y7FnqGCEa6mrN+jmI+wa7ES7AzDrnFVW3HVORa6g51WKmq5qp/7ZsVasHBNVsTi7N13gN/OJA0TXyg9Kje9licORGNoDL4ife2JhmfdAV/bR5YMX+o="
        ));
        npc.setScale(1.5F);
        Entity textDisplay = new Entity(EntityType.TEXT_DISPLAY);
        textDisplay.editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(labelText);
            meta.setHasNoGravity(true);
        });

        PodiumDisplay display = new PodiumDisplay(npc, textDisplay);
        display.show(instance, config.npcPosition(), config.labelPosition());

        return display;
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
