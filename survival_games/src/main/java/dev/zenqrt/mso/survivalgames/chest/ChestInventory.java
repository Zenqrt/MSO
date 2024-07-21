package dev.zenqrt.mso.survivalgames.chest;

import com.google.common.base.Preconditions;
import net.kyori.adventure.nbt.*;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryItemChangeEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ChestInventory extends Inventory {

    private static final Sound OPEN_SOUND = Sound.sound(SoundEvent.BLOCK_CHEST_OPEN, Sound.Source.BLOCK, 1, 1);
    private static final Sound CLOSE_SOUND = Sound.sound(SoundEvent.BLOCK_CHEST_CLOSE, Sound.Source.BLOCK, 1, 1);
    private final Instance instance;
    private final Point blockPosition;
    private final EventNode<InventoryEvent> eventNode;
    private Block block;

    ChestInventory(@NotNull Component title, Map<Integer, ItemStack> itemStackMap, Instance instance, Block block, Point blockPosition) {
        super(InventoryType.CHEST_3_ROW, title);

        this.instance = instance;
        this.block = block;
        this.blockPosition = blockPosition;

        itemStackMap.forEach(this::setItemStack);

        this.eventNode = EventNode.type("interact", EventFilter.INVENTORY, (_, inventory) -> inventory == this)
                .addListener(InventoryItemChangeEvent.class, event -> {
                    int slot = event.getSlot();
                    ListBinaryTag itemsNbt = Preconditions.checkNotNull(block.nbt()).getList("Items", BinaryTagTypes.COMPOUND);

                    for (int i = 0; i < itemsNbt.size(); i++) {
                        CompoundBinaryTag itemNbt = itemsNbt.getCompound(i);

                        if (itemNbt.getInt("Slot") != slot)
                            continue;

                        ItemStack newItem = event.getNewItem();

                        this.block = this.block.withNbt(itemNbt
                                .put("id", StringBinaryTag.stringBinaryTag(newItem.material().namespace().namespace()))
                                .put("count", IntBinaryTag.intBinaryTag(newItem.amount())));
                        this.instance.setBlock(blockPosition, this.block);
                        update();
                    }
                });

    }

    public Point getBlockPosition() {
        return blockPosition;
    }

    public EventNode<InventoryEvent> getEventNode() {
        return eventNode;
    }

    @Override
    public boolean addViewer(@NotNull Player player) {
        player.playSound(OPEN_SOUND, blockPosition);
        return super.addViewer(player);
    }

    @Override
    public boolean removeViewer(@NotNull Player player) {
        player.playSound(CLOSE_SOUND, blockPosition);
        return super.removeViewer(player);
    }

    public static ChestInventory fromBlock(Instance instance, Block block, Point blockPosition) {
        CompoundBinaryTag nbt = block.nbt();

        if (nbt == null)
            return new ChestInventory(Component.text("Chest"), Map.of(), instance, block, blockPosition);

        String title = nbt.getString("Name");

        ListBinaryTag itemsNbt = nbt.getList("Items", BinaryTagTypes.COMPOUND);
        Map<Integer, ItemStack> items = itemsNbt.stream()
                .map(tag -> (CompoundBinaryTag) tag)
                .collect(Collectors.toMap(
                        tag -> tag.getInt("Slot"),
                        tag -> ItemStack.of(
                                Objects.requireNonNull(Material.fromNamespaceId(tag.getString("id"))),
                                tag.getInt("count"))));
        return new ChestInventory(Component.text(title), items, instance, block, blockPosition);
    }
}
