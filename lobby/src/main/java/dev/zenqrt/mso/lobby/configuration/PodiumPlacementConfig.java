package dev.zenqrt.mso.lobby.configuration;

import com.google.gson.annotations.SerializedName;
import net.minestom.server.coordinate.Pos;

public record PodiumPlacementConfig(@SerializedName("npc_position") Pos npcPosition,
                                    @SerializedName("label_position") Pos labelPosition) {}
