package dev.zenqrt.mso.proxy.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import dev.zenqrt.mso.proxy.MSOProxy;
import dev.zenqrt.mso.proxy.game.MSOGame;
import dev.zenqrt.mso.proxy.utils.ConnectionUtils;

public final class JoinCommand {

    public static BrigadierCommand createBrigadierCommand(MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("join")
                        .requires(source -> source instanceof Player player && player.hasPermission("admin"))
                        .executes(context -> {
                            ConnectionUtils.connectTo(game.getCurrentGame().server(), (Player) context.getSource());
                            return 1;
                        }).then(BrigadierCommand.requiredArgumentBuilder("target", StringArgumentType.string())
                                .executes(context -> {
                                    ConnectionUtils.connectTo(game.getCurrentGame().server(), MSOProxy.getInstance().getServer().getPlayer(context.getArgument("target", String.class)).orElseThrow());
                                    return 1;
                                }))
        );
    }

}
