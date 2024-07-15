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

        this.game = new MSOGame(this, findServer("lobby"), new MSOTournamentGame[]{
                new MSOTournamentGame("Match", "match", findServer("match")),
                new MSOTournamentGame("TNT Run (Round 1)", "tnt_run", tntRunServer),
                new MSOTournamentGame("TNT Run (Round 2)", "tnt_run", tntRunServer),
                new MSOTournamentGame("TNT Run (Round 3)", "tnt_run", tntRunServer),
                new MSOTournamentGame("One in the Chamber (Round 1)", "oitc", oitcServer),
                new MSOTournamentGame("One in the Chamber (Round 2)", "oitc", oitcServer),
                new MSOTournamentGame("One in the Chamber (Round 3)", "oitc", oitcServer),
                new MSOTournamentGame("Survival Games (Round 1)", "survival_games", findServer("survival_games")),
                new MSOTournamentGame("Parkour Race", "parkour_race", findServer("parkour_race"))
        });
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
        commandManager.register(StatusCommand.createBrigadierCommand(game));
        commandManager.register(StartCommand.createBrigadierCommand(game));
        commandManager.register(SetGameCommand.createBrigadierCommand(game));

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
