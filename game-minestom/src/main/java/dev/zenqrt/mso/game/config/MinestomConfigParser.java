package dev.zenqrt.mso.game.config;

import com.google.gson.JsonObject;
import net.minestom.server.coordinate.Vec;

public final class MinestomConfigParser {

    private MinestomConfigParser() {}

    public static Vec parseVec(JsonObject jsonObject) {
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();

        return new Vec(x, y, z);
    }

}
