package dev.zenqrt.mso.game.score;

import dev.zenqrt.mso.game.player.GamePlayer;
import dev.zenqrt.mso.game.text.GameMessages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

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
         audience.playSound(Sound.sound(Key.key("minecraft:entity.experience_orb.pickup"), Sound.Source.MASTER, 1, 2), Sound.Emitter.self());
     }

     public void addPlacementScores(Map<Integer, GamePlayer> topPlayers) {
         addPlacementScore(topPlayers.get(1), FIRST_PLACE, "1st");
         addPlacementScore(topPlayers.get(2), SECOND_PLACE, "2nd");
         addPlacementScore(topPlayers.get(3), THIRD_PLACE, "3rd");
     }

     private void addPlacementScore(GamePlayer topPlayer, int score, String place) {
         if (topPlayer == null)
             return;
         addScore(topPlayer.uuid(), topPlayer.player(), score, place + " place");
     }

     public int getScore(UUID uuid) {
         return scoresGained.getOrDefault(uuid, 0);
     }

     public Map<UUID, Integer> getScoresGained() {
         return scoresGained;
     }
}
