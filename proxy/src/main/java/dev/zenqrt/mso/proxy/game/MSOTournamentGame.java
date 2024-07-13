package dev.zenqrt.mso.proxy.game;

import com.velocitypowered.api.proxy.server.RegisteredServer;

public record MSOTournamentGame(String displayName, String serverId, RegisteredServer server) {}
