package dev.zenqrt.mso.game.leaderboard;

import dev.zenqrt.mso.game.player.GamePlayer;

import java.util.Collection;

public interface LeaderboardCalculator<T extends GamePlayer> {
    /**
     * Returns sorted array of game players.
     * @param gamePlayers Collection of game players to sort
     * @param places Size of array
     * @return sorted array of size {places} of game players
     */
    T[] calculateTopPlayers(Collection<T> gamePlayers, int places);
}
