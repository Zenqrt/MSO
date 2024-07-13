package dev.zenqrt.mso.match.utils.coordinate;

import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import net.minestom.server.coordinate.Vec;

public record Region(Vec bottomCorner, Vec topCorner) {

    public static Region fromJson(JsonObject jsonObject) {
        return new Region(
                MinestomConfigParser.parseVec(jsonObject.getAsJsonObject("bottom_corner")),
                MinestomConfigParser.parseVec(jsonObject.getAsJsonObject("top_corner"))
        );
    }

}
