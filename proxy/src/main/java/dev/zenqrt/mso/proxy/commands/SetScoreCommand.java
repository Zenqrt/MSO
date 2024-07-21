package dev.zenqrt.mso.proxy.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.zenqrt.mso.proxy.game.MSOGame;
import net.kyori.adventure.text.Component;

public final class SetScoreCommand {

    public static BrigadierCommand createBrigadierCommand(ProxyServer server, MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("setscore")
                        .requires(source -> source.hasPermission("admin"))
                        .then(BrigadierCommand.requiredArgumentBuilder("target", StringArgumentType.string())
                                .then(BrigadierCommand.requiredArgumentBuilder("score", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            Player target = server.getPlayer(context.getArgument("target", String.class)).orElseThrow();
                                            game.getPlayerList().updatePlayer(target.getUniqueId(), old -> old.withScore(context.getArgument("score", Integer.class)));
                                            context.getSource().sendMessage(Component.text("Done!"));
                                            return 1;
                                        })))
        );
    }

}
