package dev.zenqrt.mso.lobby.data.configuration;

import com.google.gson.annotations.SerializedName;
import dev.zenqrt.mso.lobby.utils.coordinate.Region;

public record DungeonConfig(DungeonSelectorConfig[] selectors, @SerializedName("entrance_door") Region entranceDoor) {}
