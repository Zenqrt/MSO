package dev.zenqrt.mso.match.game;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.game.state.pregame.ConfigureIncomingPlayersGameState;
import dev.zenqrt.mso.game.state.pregame.MinestomPregameGameState;
import dev.zenqrt.mso.match.Match;
import dev.zenqrt.mso.match.game.board.Build;
import dev.zenqrt.mso.match.game.map.MatchConfig;
import dev.zenqrt.mso.match.game.map.MatchSectionArea;
import dev.zenqrt.mso.match.game.player.MatchPlayer;
import dev.zenqrt.mso.match.game.states.BuildMatchingGameState;
import dev.zenqrt.mso.match.game.states.pregame.PregameSidebarGameState;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.Instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public final class MatchGame extends MinestomGame<MatchPlayer> {

    private final Map<UUID, MatchSectionArea> playerSections = new HashMap<>();
    private final MatchConfig config;
    private final AtomicInteger currentMatchSection;

    public MatchGame(Instance instance, MatchConfig config) {
        super(instance, new HashMapGamePlayerList<>());
        this.config = config;
        this.currentMatchSection = new AtomicInteger();
    }

    @Override
    protected void populateSequence(GameStateSequence sequence) {
        GameState pregame = MinestomPregameGameState.builder(this)
                .addState(new ConfigureIncomingPlayersGameState(getEventNode(), getInstance(), player -> {
                    int index = currentMatchSection.getAndIncrement();
                    MatchSectionArea matchSection = config.matchSections()[index];
                    playerSections.put(player.getUuid(), matchSection);

                    player.setGameMode(GameMode.ADVENTURE);
                    player.setRespawnPoint(matchSection.spawnPosition().asPosition());
                }))
                .addState(new PregameSidebarGameState(getEventNode(), getPlayerList()))
                .build();
        sequence.addState(pregame);
        sequence.addState(new BuildMatchingGameState(this, getAllBuildsFromResource()));
    }

    private static Build[] getAllBuildsFromResource() {
        return new Build[] {
                getBuildFromResource("builds/creeper.json")
        };
    }

    private static Build getBuildFromResource(String path) {
        Gson gson = new Gson();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Match.class.getClassLoader().getResourceAsStream(path))))) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String displayName = jsonObject.get("display_name").getAsString();
            JsonArray blocksArray = jsonObject.getAsJsonArray("blocks");
            String[] blocks = new String[blocksArray.size()];

            for (int i = 0; i < blocksArray.size(); i++)
                blocks[i] = blocksArray.get(i).getAsString();

            return new Build(displayName, blocks);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Map<UUID, MatchSectionArea> getPlayerSections() {
        return playerSections;
    }
}
