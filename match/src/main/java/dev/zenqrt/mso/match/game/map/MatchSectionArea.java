package dev.zenqrt.mso.match.game.map;

import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import dev.zenqrt.mso.match.utils.coordinate.Region;
import net.minestom.server.coordinate.Vec;

public record MatchSectionArea(Vec spawnPosition, Region displayBoard, Region placementBoard) {

    public static MatchSectionArea fromJson(JsonObject jsonObject) {
        return new MatchSectionArea(
                MinestomConfigParser.parseVec(jsonObject.getAsJsonObject("spawn")),
                Region.fromJson(jsonObject.getAsJsonObject("display_board")),
                Region.fromJson(jsonObject.getAsJsonObject("placement_board"))
        );
    }

}
