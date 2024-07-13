package dev.zenqrt.mso.match.game.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public record MatchConfig(MatchSectionArea[] matchSections) {

    public static MatchConfig fromJson(JsonObject jsonObject) {
        return new MatchConfig(parseMatchSectionArray(jsonObject.getAsJsonArray("match_sections")));
    }

    private static MatchSectionArea[] parseMatchSectionArray(JsonArray jsonArray) {
        MatchSectionArea[] matchSections = new MatchSectionArea[jsonArray.size()];

        for (int i = 0; i < jsonArray.size(); i++) {
            matchSections[i] = MatchSectionArea.fromJson(jsonArray.get(i).getAsJsonObject());
        }

        return matchSections;
    }

}
