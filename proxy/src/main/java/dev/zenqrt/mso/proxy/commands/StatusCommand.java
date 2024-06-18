package dev.zenqrt.mso.proxy.commands;

import com.velocitypowered.api.command.BrigadierCommand;
import dev.zenqrt.mso.proxy.game.MSOGame;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class StatusCommand {

    public static BrigadierCommand createBrigadierCommand(MSOGame game) {
        return new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("status")
//                        .requires(source -> source.hasPermission("mso.command.status"))
                        .then(BrigadierCommand.literalArgumentBuilder("server")
                                .executes(context -> {
                                    context.getSource().sendMessage(
                                            Component.text("""
                                    Game Server Status: {game_server_status}""").replaceText(builder ->
                                                    builder.matchLiteral("{game_server_status}").replacement(
                                                            true ? Component.text("Connected", NamedTextColor.GREEN) : Component.text("Pending...", NamedTextColor.DARK_GRAY))));
                                    return 1;
                                }))
        );
    }

}
