package dev.zenqrt.mso.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.zenqrt.mso.proxy.commands.*;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.MSOTournamentGame;
import dev.zenqrt.mso.proxy.game.player.MSOGamePlayer;
import org.slf4j.Logger;

@Plugin(
        id = "msoproxy",
        name = "MSOProxy",
        version = "1.0-SNAPSHOT",
        authors = {"Walmqrt"}
)
public final class MSOProxy {

    private static MSOProxy instance;
    private final ProxyServer server;
    private final Logger logger;
    private final MSOGame game;

    @Inject
    public MSOProxy(ProxyServer server, Logger logger) {
        instance = this;
        this.server = server;
        this.logger = logger;

        RegisteredServer tntRunServer = findServer("tnt_run");
        RegisteredServer oitcServer = findServer("oitc");
        RegisteredServer matchServer = findServer("match");
        RegisteredServer spleefServer = findServer("spleef");
        RegisteredServer parkourRaceServer = findServer("parkour_race");

        this.game = new MSOGame(this, findServer("lobby"), new MSOTournamentGame[]{
                createTournamentGame("TNT Run (R1)", tntRunServer),
                createTournamentGame("TNT Run (R2)", tntRunServer),
                createTournamentGame("TNT Run (R3)", tntRunServer),
                createTournamentGame("One in the Chamber (R1)", oitcServer),
                createTournamentGame("One in the Chamber (R2)", oitcServer),
                createTournamentGame("Match", matchServer),
                createTournamentGame("Spleef (R1)", spleefServer),
                createTournamentGame("Spleef (R2)", spleefServer),
                createTournamentGame("Parkour Race", parkourRaceServer)
        });
    }

    private MSOTournamentGame createTournamentGame(String displayName, RegisteredServer server) {
        return new MSOTournamentGame(displayName, server.getServerInfo().getName(), server);
    }

    private RegisteredServer findServer(String serverName) {
        return server.getServer(serverName).orElseThrow();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignored) {
        logger.info("MSOProxy is initializing...");

        game.start();

        CommandManager commandManager = server.getCommandManager();
        commandManager.register(LobbyCommand.createBrigadierCommand(game.getLobbyServer()));
        commandManager.register(JoinCommand.createBrigadierCommand(game));
        commandManager.register(StartCommand.createBrigadierCommand(game));
        commandManager.register(JoinAllCommand.createBrigadierCommand(this, game));
        commandManager.register(GetScoresCommand.createBrigadierCommand(game));
        commandManager.register(SetScoreCommand.createBrigadierCommand(server, game));

        logger.info("MSOProxy has initialized.");
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        logger.info("Hi");
        game.getPlayerList().addPlayer(new MSOGamePlayer(event.getPlayer().getUniqueId(), event.getPlayer(), 0));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        game.getPlayerList().removePlayer(event.getPlayer().getUniqueId());
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        logger.info("Messaged!!!");
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public MSOGame getGame() {
        return game;
    }

    public static MSOProxy getInstance() {
        return instance;
    }
}
