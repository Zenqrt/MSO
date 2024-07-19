package dev.zenqrt.mso.game.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public final class MinestomConfigParser {

    private MinestomConfigParser() {}

    public static Vec parseVec(JsonObject jsonObject) {
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();

        return new Vec(x, y, z);
    }

    public static Pos parsePos(JsonObject jsonObject) {
        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();
        float yaw = getFloatOrDefault(jsonObject, "yaw", 0);
        float pitch = getFloatOrDefault(jsonObject, "pitch", 0);

        return new Pos(x, y, z, yaw, pitch);
    }

    private static float getFloatOrDefault(JsonObject jsonObject, String name, float defaultValue) {
        JsonElement element = jsonObject.get(name);
        return element == null ? defaultValue : element.getAsFloat();
    }

}
