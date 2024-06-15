package dev.zenqrt.mso.game.messages;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.zenqrt.mso.game.score.ScoreKeeper;

public final class PluginMessageFactory {

    public static byte[] gameEndScores(ScoreKeeper scoreKeeper) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        scoreKeeper.getScoresGained().forEach((uuid, score) -> {
            output.writeUTF(uuid.toString());
            output.writeInt(score);
        });

        return output.toByteArray();
    }

}
