package dev.zenqrt.mso.game.commands;

import dev.zenqrt.mso.game.permission.Permissions;
import dev.zenqrt.mso.text.Messages;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.location.RelativeVec;

public final class TeleportCommand extends Command {

    public TeleportCommand() {
        super("teleport", "tp");

        setCondition((sender, _) -> sender instanceof Player && sender.hasPermission(Permissions.ADMIN));

        var targetArgument = ArgumentType.String("target");
        var positionArgument = ArgumentType.RelativeVec3("position");

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

        addSyntax((sender, context) -> {
            Player player = (Player) sender;
            RelativeVec relativeVec = context.get(positionArgument);
            Pos position = relativeVec.from(Pos.ZERO).asPosition()
                    .withPitch(player.getPosition().pitch())
                    .withYaw(player.getPosition().yaw());

            player.teleport(position);
            player.sendMessage(Messages.success("Teleported to (" + position.x() + ", " + position.y() + ", " + position.z() + ")."));
        }, positionArgument);
    }

}
