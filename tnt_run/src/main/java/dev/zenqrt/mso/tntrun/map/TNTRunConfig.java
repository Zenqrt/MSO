package dev.zenqrt.mso.tntrun.map;

import com.google.gson.JsonObject;

public record TNTRunConfig(int bottomYLevel) {

    public TNTRunConfig(JsonObject jsonObject) {
        this(jsonObject.get("bottom_y_level").getAsInt());
    }

}
