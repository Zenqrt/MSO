package dev.zenqrt.mso.lobby.configuration;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record MSOLobbyConfig(@SerializedName("podium_placements") List<PodiumPlacementConfig> podiumPlacements,
                             @SerializedName("dungeon_settings") DungeonConfig dungeonSettings,
                             @SerializedName("rainbow_man_settings") RainbowManConfig rainbowManSettings) {}
