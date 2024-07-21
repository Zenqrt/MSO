package dev.zenqrt.mso.survivalgames.chest;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.Map;

public final class ChestFeature {

    private static final Map<Point, ChestInventory> openedChests = new HashMap<>();

    public static EventNode<InstanceEvent> createEventNode() {
        EventNode<InstanceEvent> eventNode = EventNode.type("chest", EventFilter.INSTANCE);

        eventNode.addListener(EventListener.builder(PlayerBlockInteractEvent.class)
                .filter(event -> event.getBlock().compare(Block.CHEST))
                .handler(event -> {
                    Point position = event.getBlockPosition();
                    Player player = event.getPlayer();

                    if (openedChests.get(position) != null) {
                        player.openInventory(openedChests.get(position));
                        return;
                    }

                    ChestInventory chestInventory = ChestInventory.fromBlock(event.getInstance(), event.getBlock(), position);

                    player.openInventory(chestInventory);
                    openedChests.put(position, chestInventory);
                    MinecraftServer.getGlobalEventHandler().addChild(chestInventory.getEventNode());
                }).build());
        eventNode.addListener(InventoryCloseEvent.class, event -> {
            if (!(event.getInventory() instanceof ChestInventory inventory && inventory.getViewers().isEmpty()))
                return;

            openedChests.remove(inventory.getBlockPosition());
        });

        return eventNode;
    }

}
