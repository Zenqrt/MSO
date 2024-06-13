package dev.zenqrt.mso.lobby.gson.deserializers;

import com.google.gson.*;
import net.minestom.server.coordinate.Vec;

import java.lang.reflect.Type;

public final class VecDeserializer implements JsonDeserializer<Vec> {

    @Override
    public Vec deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        return new Vec(
                jsonObject.get("x").getAsDouble(),
                jsonObject.get("y").getAsDouble(),
                jsonObject.get("z").getAsDouble()
        );
    }
}
