package dev.zenqrt.mso.proxy.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;

public final class JoinAllCommand {

    public static BrigadierCommand createBrigadierCommand(MSOProxy proxy, MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("joinall")
                        .requires(source -> source.hasPermission("admin"))
                        .executes(_ -> {
                            proxy.getServer().getAllPlayers().forEach(player ->
                                    ConnectionUtils.connectTo(game.getCurrentGame().server(), player));
                            return 1;
                        })
        );
    }

}
