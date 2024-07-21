package dev.zenqrt.mso.parkourrace.map;

import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import dev.zenqrt.mso.parkourrace.utils.coordinate.Region;
import net.minestom.server.coordinate.Pos;

public record Checkpoint(Region region, Pos spawn) {

    public static Checkpoint fromJson(JsonObject jsonObject) {
        return new Checkpoint(
                Region.fromJson(jsonObject.getAsJsonObject("region")),
                MinestomConfigParser.parsePos(jsonObject.getAsJsonObject("spawn"))
        );
    }

}
