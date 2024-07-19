package dev.zenqrt.mso.oitc.game.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import net.minestom.server.coordinate.Vec;

public record OneInTheChamberConfig(Vec[] spawnPositions) {

    public static OneInTheChamberConfig fromJson(JsonObject jsonObject) {
        JsonArray spawnsArray = jsonObject.getAsJsonArray("spawns");
        Vec[] spawnPositions = new Vec[spawnsArray.size()];

        for (int i = 0; i < spawnsArray.size(); i++) {
            spawnPositions[i] = MinestomConfigParser.parseVec(spawnsArray.get(i).getAsJsonObject());
        }

        return new OneInTheChamberConfig(spawnPositions);
    }

}
