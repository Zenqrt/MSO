package dev.zenqrt.mso.proxy;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.zenqrt.mso.proxy.commands.JoinCommand;
import dev.zenqrt.mso.proxy.commands.LobbyCommand;
import dev.zenqrt.mso.proxy.commands.StartCommand;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.MSOTournamentGame;
import dev.zenqrt.mso.proxy.game.player.GamePlayer;
import dev.zenqrt.mso.proxy.messages.ServerTransferHandler;
import org.slf4j.Logger;

@Plugin(
        id = "msoproxy",
        name = "MSOProxy",
        version = "1.0-SNAPSHOT",
        authors = {"Walmqrt"}
)
public final class MSOProxy {

    private final ProxyServer server;
    private final Logger logger;
    private final MSOGame game;

    @Inject
    public MSOProxy(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.game = new MSOGame(this, findServer("lobby"), new MSOTournamentGame[]{
                new MSOTournamentGame("TNT Run", findServer("tnt_run")),
                new MSOTournamentGame("One in the Chamber", findServer("oitc")),
                new MSOTournamentGame("Match", findServer("match")),
                new MSOTournamentGame("Survival Games", findServer("survival_games")),
                new MSOTournamentGame("Parkour Race", findServer("parkour_race"))
        });
    }

    private RegisteredServer findServer(String serverName) {
        return server.getServer(serverName).orElseThrow();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent ignored) {
        logger.info("MSOProxy is initializing...");

        game.start();
        server.getEventManager().register(this, new ServerTransferHandler(this, game));

        CommandManager commandManager = server.getCommandManager();
        commandManager.register(LobbyCommand.createBrigadierCommand(game.getLobbyServer()));
        commandManager.register(JoinCommand.createBrigadierCommand(game));
        commandManager.register(StartCommand.createBrigadierCommand(game));

        logger.info("MSOProxy has initialized.");
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        game.getPlayerList().addPlayer(new GamePlayer(event.getPlayer().getUniqueId(), 0));
    }

    @Subscribe
    public void onDisconnect(DisconnectEvent event) {
        game.getPlayerList().removePlayer(event.getPlayer().getUniqueId());
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
}
