package dev.zenqrt.mso.survivalgames.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

import java.util.ArrayList;
import java.util.List;

public record SurvivalGamesConfig(Pos spawn, List<Vec> chestPositions) {

    public static SurvivalGamesConfig fromJson(JsonObject jsonObject) {
        List<Vec> chestPositions = new ArrayList<>();
        JsonArray jsonArray = jsonObject.getAsJsonArray("chests");

        for (JsonElement element : jsonArray) {
            chestPositions.add(MinestomConfigParser.parseVec(element.getAsJsonObject()));
        }

        return new SurvivalGamesConfig(
                MinestomConfigParser.parsePos(jsonObject.getAsJsonObject("spawn")),
                chestPositions
        );
    }

}
