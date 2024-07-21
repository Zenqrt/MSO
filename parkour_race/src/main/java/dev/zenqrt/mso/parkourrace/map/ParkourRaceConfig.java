package dev.zenqrt.mso.parkourrace.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import dev.zenqrt.mso.parkourrace.utils.coordinate.Region;
import net.minestom.server.coordinate.Pos;

public record ParkourRaceConfig(Pos spawnPosition, Checkpoint[] checkpoints, Region finish) {

    public static ParkourRaceConfig fromJson(JsonObject jsonObject) {
        return new ParkourRaceConfig(
                MinestomConfigParser.parsePos(jsonObject.getAsJsonObject("spawn")),
                parseCheckpointsArray(jsonObject.getAsJsonArray("checkpoints")),
                Region.fromJson(jsonObject.getAsJsonObject("finish"))
        );
    }

    private static Checkpoint[] parseCheckpointsArray(JsonArray jsonArray) {
        Checkpoint[] checkpoints = new Checkpoint[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            checkpoints[i] = Checkpoint.fromJson(jsonArray.get(i).getAsJsonObject());
        }

        return checkpoints;
    }

}
