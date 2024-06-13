package dev.zenqrt.mso.lobby.item;

import dev.zenqrt.mso.lobby.item.items.RainbowSheepinatorItem;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;

import java.util.ArrayList;
import java.util.List;

public final class ItemRegistry {

    private static final List<CustomItem> ITEMS = new ArrayList<>();
    public static final CustomItem RAINBOW_SHEEPINATOR = registerItem(new RainbowSheepinatorItem());

    public static EventNode<Event> registerItemEvents() {
        EventNode<Event> eventNode = EventNode.all("custom_items");
        ITEMS.forEach(item -> item.registerEventNodes(eventNode));

        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
        return eventNode;
    }

    private static CustomItem registerItem(CustomItem item) {
        ITEMS.add(item);
        return item;
    }
}
