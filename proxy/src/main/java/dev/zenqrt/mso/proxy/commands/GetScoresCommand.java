package dev.zenqrt.mso.proxy.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import dev.zenqrt.mso.proxy.game.MSOGame;
import net.kyori.adventure.text.Component;

public final class GetScoresCommand {

    public static BrigadierCommand createBrigadierCommand(MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("getscores")
                        .requires(source -> source.hasPermission("admin"))
                        .executes(context -> {
                            game.getPlayerList().forEach(gamePlayer -> context.getSource().sendMessage(
                                    Component.text(gamePlayer.player().getUsername() + ": " + gamePlayer.score())
                            ));
                            return 1;
                        })
        );
    }

}
