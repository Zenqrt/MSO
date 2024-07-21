package dev.zenqrt.mso.survivalgames.game.states;

import dev.zenqrt.mso.game.state.GameState;
import dev.zenqrt.mso.survivalgames.chest.loot.LootTable;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class FillChestsGameState extends GameState {

    private final Instance instance;
    private final List<Vec> chestPositions;
    private final LootTable lootTable;

    public FillChestsGameState(Instance instance, List<Vec> chestPositions, LootTable lootTable) {
        this.instance = instance;
        this.chestPositions = chestPositions;
        this.lootTable = lootTable;
    }

    @Override
    protected void onStateStart() {
        for (Vec chestPosition : chestPositions) {
            List<ItemStack> loot = lootTable.populateLoot();
            Map<Integer, ItemStack> chestItems = new HashMap<>();

            for (ItemStack itemStack : loot) {
                int slot;

                do {
                    slot = ThreadLocalRandom.current().nextInt(27);

                    if (chestItems.containsKey(slot))
                        continue;

                    chestItems.put(slot, itemStack);
                    break;
                } while (true);
            }

            Block block = instance.getBlock(chestPosition);
            CompoundBinaryTag nbt = Objects.requireNonNull(block.nbt());
            ListBinaryTag itemsNbt = nbt.getList("Items");

            for (Map.Entry<Integer, ItemStack> entry : chestItems.entrySet()) {
                int slot = entry.getKey();
                ItemStack itemStack = entry.getValue();

                String materialId = itemStack.material().namespace().namespace();
                int count = itemStack.amount();

                label:
                {
                    for (int i = 0; i < itemsNbt.size(); i++) {
                        CompoundBinaryTag itemNbt = itemsNbt.getCompound(i);

                        if (itemNbt.getInt("Slot") == slot) {
                            itemsNbt = itemsNbt.set(i, itemNbt.putString("id", materialId).putInt("count", itemStack.amount()), null);
                            break label;
                        }
                    }

                    CompoundBinaryTag newItemNbt = CompoundBinaryTag.empty();
                    newItemNbt.putInt("Slot", slot);
                    newItemNbt.putString("id", materialId);
                    newItemNbt.putInt("count", count);

                    itemsNbt = itemsNbt.add(newItemNbt
                            .putInt("Slot", slot)
                            .putString("id", materialId)
                            .putInt("count", count));
                }
            }

            Block block1 = block.withNbt(nbt.put("Items", itemsNbt));
            System.out.println(block1.nbt().getList("Items"));
            instance.setBlock(chestPosition, block1);
        }
    }
}
