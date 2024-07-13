package dev.zenqrt.mso.match.game.board;

import dev.zenqrt.mso.match.utils.coordinate.Region;

public final class Board {

    private final Region region;
    private String[] placedBlockIds;

    public Board(Region region, String[] placedBlockIds) {
        this.region = region;
        this.placedBlockIds = placedBlockIds;
    }

    public void setPlacedBlockId(int index, String id) {
        placedBlockIds[index] = id;
    }

    public void setPlacedBlockIds(String[] placedBlockIds) {
        this.placedBlockIds = placedBlockIds;
    }

    public String[] getPlacedBlockIds() {
        return placedBlockIds;
    }

    public Region getRegion() {
        return region;
    }
}

