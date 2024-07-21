package dev.zenqrt.mso.proxy.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.server.PingOptions;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.MSOTournamentGame;
import dev.zenqrt.mso.text.Messages;

import java.util.concurrent.TimeUnit;

public final class StartCommand {

    public static BrigadierCommand createBrigadierCommand(MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("start")
                        .requires(source -> source.hasPermission("mso.command.start"))
                        .executes(context -> {
                            CommandSource source = context.getSource();
                            source.sendMessage(Messages.action("Checking server status..."));

                            MSOTournamentGame currentGame = game.getCurrentGame();
                            PingOptions pingOptions = PingOptions.builder()
                                    .timeout(5, TimeUnit.SECONDS)
                                    .build();

                            currentGame.server().ping(pingOptions)
                                    .whenComplete((ping, _) -> {
                                        if (ping == null) {
                                            source.sendMessage(Messages.error(currentGame.displayName() + " server is currently offline! Aborting start."));
                                            return;
                                        }

                                        source.sendMessage(Messages.action("Starting game..."));
                                        game.switchToNextState();
                                    });

                            return 1;
                        })
        );
    }

}
