package dev.zenqrt.mso.lobby.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;

public abstract class CustomItem {

    private static final Tag<String> ITEM_ID_TAG = Tag.String("item_id");
    private final String id;
    private final ItemStack.Builder itemStackBuilder;
    protected final EventNode<PlayerEvent> playerEventNode;
    protected final EventNode<ItemEvent> itemEventNode;
    protected EventNode<Event> parentNode;

    public CustomItem(String id, ItemStack.Builder itemStackBuilder) {
        this.id = id;
        this.itemStackBuilder = itemStackBuilder
                .set(ITEM_ID_TAG, id);
        this.playerEventNode = EventNode.event(id + "_player", EventFilter.PLAYER, event -> checkItem(event.getPlayer().getItemInMainHand()));
        this.itemEventNode = EventNode.event(id + "_item", EventFilter.ITEM, event -> checkItem(event.getItemStack()));

        registerEvents();
    }

    public CustomItem(String id, Material material, Component displayName) {
        this(id, ItemStack.builder(material).set(ItemComponent.ITEM_NAME, displayName.decoration(TextDecoration.ITALIC, false)));
    }

    private boolean checkItem(ItemStack itemStack) {
        String id = itemStack.getTag(ITEM_ID_TAG);
        return id != null && id.equals(this.id);
    }

    protected abstract void registerEvents();

    public void registerEventNodes(EventNode<Event> parentNode) {
        parentNode.addChild(playerEventNode);
        parentNode.addChild(itemEventNode);

        this.parentNode = parentNode;
    }

    public ItemStack buildItemStack() {
        return itemStackBuilder.build();
    }
}
