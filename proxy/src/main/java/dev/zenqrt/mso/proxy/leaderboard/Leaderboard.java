package dev.zenqrt.mso.proxy.leaderboard;

import dev.zenqrt.mso.proxy.game.player.GamePlayer;
import dev.zenqrt.mso.proxy.game.player.GamePlayerList;

public final class Leaderboard {

    private final int maxPlaces;
    private final GamePlayerList playerList;
    private final LeaderboardCalculator leaderboardCalculator;
    private final GamePlayer[] topPlayers;

    public Leaderboard(int maxPlaces, GamePlayerList playerList, LeaderboardCalculator leaderboardCalculator) {
        this.maxPlaces = maxPlaces;
        this.playerList = playerList;
        this.leaderboardCalculator = leaderboardCalculator;
        this.topPlayers = new GamePlayer[maxPlaces];
    }

    public void update() {
        GamePlayer[] newTopPlayers = leaderboardCalculator.calculateTopPlayers(playerList.getPlayers().values(), maxPlaces);

        for (int i = 0; i < maxPlaces; i++) {
            if (i >= newTopPlayers.length) {
                topPlayers[i] = null;
                continue;
            }

            topPlayers[i] = newTopPlayers[i];
        }
    }

    public GamePlayer[] getTopPlayers() {
        return topPlayers;
    }

}
