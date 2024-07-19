package map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public record ParkourRaceConfig(Pos spawnPosition, Pos[] checkpoints, Vec finish) {

    public static ParkourRaceConfig fromJson(JsonObject jsonObject) {
        return new ParkourRaceConfig(
                MinestomConfigParser.parsePos(jsonObject.getAsJsonObject("spawn")),
                parseCheckpointsArray(jsonObject.getAsJsonArray("checkpoints")),
                MinestomConfigParser.parseVec(jsonObject.getAsJsonObject("finish"))
        );
    }

    private static Pos[] parseCheckpointsArray(JsonArray jsonArray) {
        Pos[] checkpoints = new Pos[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            checkpoints[i] = MinestomConfigParser.parsePos(jsonArray.get(i).getAsJsonObject());
        }

        return checkpoints;
    }

}
