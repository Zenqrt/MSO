package dev.zenqrt.mso.lobby.configuration;

import com.google.gson.annotations.SerializedName;
import net.minestom.server.coordinate.Vec;

public record DungeonSelectorConfig(@SerializedName("display_position") Vec displayPosition,
                                    @SerializedName("top_button_position") Vec topButtonPosition,
                                    @SerializedName("bottom_button_position") Vec bottomButtonPosition) {}
