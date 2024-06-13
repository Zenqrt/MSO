package dev.zenqrt.mso.proxy.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;

public final class JoinCommand {

    public static BrigadierCommand createBrigadierCommand(MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("join")
                        .requires(source -> source instanceof Player)
                        .executes(context -> {
                            ConnectionUtils.connectTo(game.getCurrentGame().server(), (Player) context.getSource());
                            return 1;
                        })
        );
    }

}
