package dev.zenqrt.mso.proxy.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.game.state.states.IntermissionGameState;
import dev.zenqrt.mso.proxy.utils.text.Messages;

public final class StartCommand {

    public static BrigadierCommand createBrigadierCommand(MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("start")
                        .requires(source -> source.hasPermission("mso.command.start"))
                        .requires(ignored -> game.getState() instanceof IntermissionGameState)
                        .executes(context -> {
                            context.getSource().sendMessage(Messages.action("Starting game..."));
                            game.switchToNextState();
                            return 1;
                        })
        );
    }

}
