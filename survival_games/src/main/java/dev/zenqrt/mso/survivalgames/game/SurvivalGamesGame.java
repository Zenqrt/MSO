package dev.zenqrt.mso.survivalgames.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.MinestomGame;
import dev.zenqrt.mso.game.player.HashMapGamePlayerList;
import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.game.state.GameStateSequence;
import dev.zenqrt.mso.game.state.pregame.ConfigureIncomingPlayersGameState;
import dev.zenqrt.mso.game.state.pregame.MinestomPregameGameState;
import dev.zenqrt.mso.survivalgames.SurvivalGames;
import dev.zenqrt.mso.survivalgames.chest.loot.LootTable;
import dev.zenqrt.mso.survivalgames.config.SurvivalGamesConfig;
import dev.zenqrt.mso.survivalgames.game.player.SurvivalGamesPlayer;
import dev.zenqrt.mso.survivalgames.game.states.FightingGameState;
import dev.zenqrt.mso.survivalgames.game.states.FillChestsGameState;
import net.minestom.server.entity.GameMode;
import net.minestom.server.instance.Instance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

public final class SurvivalGamesGame extends MinestomGame<SurvivalGamesPlayer> {

    private static final LootTable WEAK_LOOT;

    static {
        Gson gson = new Gson();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(SurvivalGames.class.getClassLoader().getResourceAsStream("loot_tables/weak_loot.json"))))) {
            WEAK_LOOT = LootTable.fromJson(gson.fromJson(reader, JsonObject.class));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private final SurvivalGamesConfig config;

    public SurvivalGamesGame(Instance instance, SurvivalGamesConfig config) {
        super(instance, new HashMapGamePlayerList<>());

        this.config = config;
    }

    @Override
    protected void populateSequence(GameStateSequence sequence) {
        GameState pregame = MinestomPregameGameState.builder(this)
                .addState(new ConfigureIncomingPlayersGameState(getEventNode(), player -> {
                    player.setGameMode(GameMode.CREATIVE);
                    player.setRespawnPoint(config.spawn());
                }))
                .build();

        sequence.addState(pregame);
        sequence.addState(new FillChestsGameState(getInstance(), config.chestPositions(), WEAK_LOOT));
        sequence.addState(new FightingGameState(getEventNode()));
    }
}
