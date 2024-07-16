package dev.zenqrt.mso.game.commands;

import dev.zenqrt.mso.game.permission.Permissions;
import dev.zenqrt.mso.text.Messages;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public final class TeleportCommand extends Command {

    public TeleportCommand() {
        super("teleport", "tp");

        setCondition((sender, _) -> sender instanceof Player && sender.hasPermission(Permissions.ADMIN));

        var targetArgument = ArgumentType.String("target");

        addSyntax((sender, context) -> {
            Player player = (Player) sender;

            String targetName = context.get(targetArgument);
            Player target = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(targetName);

            if (target == null) {
                player.sendMessage(Messages.error(targetName + " is not online or does not exist!"));
                return;
            }

            if (player.getInstance() != target.getInstance()) {
                player.setInstance(target.getInstance(), target.getPosition());
            } else {
                player.teleport(target.getPosition());
            }

            player.sendMessage(Messages.success("Teleported to " + target.getUsername()));
        }, targetArgument);
    }

}
