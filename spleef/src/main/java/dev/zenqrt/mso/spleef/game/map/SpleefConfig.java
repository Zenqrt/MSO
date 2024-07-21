package dev.zenqrt.mso.spleef.game.map;

import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import net.minestom.server.coordinate.Pos;

public record SpleefConfig(Pos spawn, int bottomYLevel) {

    public static SpleefConfig fromJson(JsonObject jsonObject) {
        return new SpleefConfig(
                MinestomConfigParser.parsePos(jsonObject.getAsJsonObject("spawn")),
                jsonObject.get("bottom_y_level").getAsInt()
        );
    }

}
