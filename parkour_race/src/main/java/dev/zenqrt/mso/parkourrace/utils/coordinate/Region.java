package dev.zenqrt.mso.parkourrace.utils.coordinate;

import com.google.gson.JsonObject;
import dev.zenqrt.mso.game.config.MinestomConfigParser;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

public record Region(Vec bottomCorner, Vec topCorner) {

    public static Region fromJson(JsonObject jsonObject) {
        return new Region(
                MinestomConfigParser.parseVec(jsonObject.getAsJsonObject("bottom_corner")),
                MinestomConfigParser.parseVec(jsonObject.getAsJsonObject("top_corner"))
        );
    }

    public boolean isInRegion(Point point) {
        return (point.x() >= this.bottomCorner().x() && point.x() <= this.topCorner().x() + 1)
                && (point.y() >= this.bottomCorner().y() && point.y() <= this.topCorner().y())
                && (point.z() >= this.bottomCorner().z() && point.z() <= this.topCorner().z() + 1);
    }

}

