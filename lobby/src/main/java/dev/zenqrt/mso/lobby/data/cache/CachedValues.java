package dev.zenqrt.mso.lobby.data.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CachedValues {

    private static final Map<UUID, Integer> SCORES = new HashMap<>();

    private CachedValues() {}
    private static String NEXT_GAME = "None";

    public static String getNextGame() {
        return NEXT_GAME;
    }

    public static void setNextGame(String nextGame) {
        NEXT_GAME = nextGame;
    }

    public static void cacheScore(UUID uuid, int score) {
        SCORES.put(uuid, score);
    }

    public static int getScore(UUID uuid) {
        return SCORES.getOrDefault(uuid, 0);
    }
}
