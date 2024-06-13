package dev.zenqrt.mso.lobby.gson.deserializers;

import com.google.gson.*;
import net.minestom.server.coordinate.Pos;

import java.lang.reflect.Type;

public final class PosDeserializer implements JsonDeserializer<Pos> {

    @Override
    public Pos deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        double x = jsonObject.get("x").getAsDouble();
        double y = jsonObject.get("y").getAsDouble();
        double z = jsonObject.get("z").getAsDouble();

        if (jsonObject.has("yaw")) {
            return new Pos(x, y, z, jsonObject.get("yaw").getAsFloat(), jsonObject.get("pitch").getAsFloat());
        } else {
            return new Pos(x, y, z);
        }
    }
}
