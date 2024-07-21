package dev.zenqrt.mso.lobby.data.configuration;

import com.google.gson.annotations.SerializedName;
import net.minestom.server.coordinate.Pos;

public record RainbowManConfig(@SerializedName("npc_position") Pos npcPosition,
                               @SerializedName("sheep_position") Pos sheepPosition) {
}
