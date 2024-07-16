package dev.zenqrt.mso.lobby.commands;

import dev.zenqrt.mso.text.Messages;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public final class GamemodeCommand extends Command {

    public GamemodeCommand() {
        super("gamemode", "gm");

        setCondition((sender, _) -> sender instanceof Player && sender.hasPermission("admin"));

        var modeArgument = ArgumentType.Enum("mode", GameMode.class);

        addSyntax((sender, context) -> {
            GameMode gameMode = context.get(modeArgument);

            ((Player) sender).setGameMode(gameMode);
            sender.sendMessage(Messages.success("Set your game mode to " + gameMode.name().toLowerCase()));
        }, modeArgument);
    }

}
