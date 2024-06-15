package dev.zenqrt.mso.proxy.leaderboard;

import dev.zenqrt.mso.game.leaderboard.LeaderboardCalculator;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayer;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayerList;

public final class Leaderboard {

    private final int maxPlaces;
    private final MSOGamePlayerList playerList;
    private final LeaderboardCalculator<MSOGamePlayer> leaderboardCalculator;
    private final MSOGamePlayer[] topPlayers;

    public Leaderboard(int maxPlaces, MSOGamePlayerList playerList, LeaderboardCalculator<MSOGamePlayer> leaderboardCalculator) {
        this.maxPlaces = maxPlaces;
        this.playerList = playerList;
        this.leaderboardCalculator = leaderboardCalculator;
        this.topPlayers = new MSOGamePlayer[maxPlaces];
    }

    public void update() {
        MSOGamePlayer[] newTopPlayers = leaderboardCalculator.calculateTopPlayers(playerList.getPlayers().values(), maxPlaces);

        for (int i = 0; i < maxPlaces; i++) {
            if (i >= newTopPlayers.length) {
                topPlayers[i] = null;
                continue;
            }

            topPlayers[i] = newTopPlayers[i];
        }
    }

    public MSOGamePlayer[] getTopPlayers() {
        return topPlayers;
    }

}
