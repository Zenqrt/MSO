package dev.zenqrt.mso.proxy.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.MSOTournamentGame;
import dev.zenqrt.mso.text.Messages;

public final class SetGameCommand {

    public static BrigadierCommand createBrigadierCommand(MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("setgame")
                        .then(BrigadierCommand.requiredArgumentBuilder("index", IntegerArgumentType.integer())
                                .executes(context -> {
                                    int index = context.getArgument("index", Integer.class);
                                    MSOTournamentGame currentGame = game.setCurrentGame(index);
                                    context.getSource().sendMessage(Messages.success("Changed game to " + currentGame.displayName() + "!"));
                                    return 1;
                                }))
        );
    }

}
