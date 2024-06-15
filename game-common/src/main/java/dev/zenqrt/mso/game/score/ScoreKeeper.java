package dev.zenqrt.mso.game.score;

import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.text.GameMessages;
import net.kyori.adventure.audience.Audience;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ScoreKeeper {

     private static final int FIRST_PLACE = 10;
     private static final int SECOND_PLACE = 8;
     private static final int THIRD_PLACE = 6;
     private final Map<UUID, Integer> scoresGained = new HashMap<>();

     public void addScore(UUID uuid, Audience audience, int score, String reason) {
         scoresGained.merge(uuid, score, Integer::sum);
         audience.sendMessage(GameMessages.scoreAdded(score, reason));
     }

     public void addPlacementScores(GamePlayer[] topPlayers) {
         addPlacementScore(topPlayers[0], FIRST_PLACE, "1st");
         addPlacementScore(topPlayers[1], SECOND_PLACE, "2nd");
         addPlacementScore(topPlayers[2], THIRD_PLACE, "3rd");
     }

     private void addPlacementScore(GamePlayer topPlayer, int score, String place) {
         addScore(topPlayer.uuid(), topPlayer.player(), score, place + " place");
     }

     public Map<UUID, Integer> getScoresGained() {
         return scoresGained;
     }
}
