package dev.zenqrt.mso.proxy.leaderboard;

import dev.zenqrt.mso.proxy.game.player.GamePlayer;

import java.util.Collection;

public interface LeaderboardCalculator {
    GamePlayer[] calculateTopPlayers(Collection<GamePlayer> gamePlayers, int places);
}
