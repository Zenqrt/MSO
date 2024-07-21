package dev.zenqrt.mso.lobby.commands;

import dev.zenqrt.mso.lobby.item.ItemRegistry;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;

public final class GiveRainbowCommand extends Command {

    public GiveRainbowCommand() {
        super("giverainbow");
        setCondition((sender, _) -> sender instanceof Player && sender.hasPermission("admin"));

        var targetArgument = ArgumentType.String("target");

        addSyntax((_, context) -> {
            Player target = MinecraftServer.getConnectionManager().getOnlinePlayerByUsername(context.get(targetArgument));

            target.getInventory().addItemStack(ItemRegistry.RAINBOW_SHEEPINATOR.buildItemStack());
        }, targetArgument);
    }

}
