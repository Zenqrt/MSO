package dev.zenqrt.mso.tntrun.map;

import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import net.minestom.server.coordinate.Pos;

public record TNTRunConfig(Pos spawnPosition, int bottomYLevel) {

    public TNTRunConfig(JsonObject jsonObject) {
        this(
                MinestomConfigParser.parseVec(jsonObject.getAsJsonObject("spawn")).asPosition(),
                jsonObject.get("bottom_y_level").getAsInt()
        );
    }

}
