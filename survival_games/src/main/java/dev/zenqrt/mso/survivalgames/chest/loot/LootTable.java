package dev.zenqrt.mso.survivalgames.chest.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public record LootTable(int minItems, int maxItems, List<ItemEntry> itemEntries) {

    public static LootTable fromJson(JsonObject jsonObject) {
        JsonArray jsonArray = jsonObject.getAsJsonArray("items");
        List<ItemEntry> itemEntries = new ArrayList<>();
        jsonArray.forEach(element -> itemEntries.add(ItemEntry.fromJson(element.getAsJsonObject())));
        return new LootTable(
                jsonObject.get("min_items").getAsInt(),
                jsonObject.get("max_items").getAsInt(),
                itemEntries
        );
    }

    public List<ItemStack> populateLoot() {
        int size = ThreadLocalRandom.current().nextInt(minItems, maxItems);
        List<ItemStack> itemStacks = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            itemStacks.add(getRandomItem());
        }

        return itemStacks;
    }

    private ItemStack getRandomItem() {
        List<ItemStack> itemStacks = itemEntries.stream()
                .flatMap(entry -> Stream.generate(() -> entry.itemStack).limit(entry.weight))
                .toList();

        return itemStacks.get(ThreadLocalRandom.current().nextInt(itemStacks.size()));
    }

    public record ItemEntry(ItemStack itemStack, int weight) {

        public static ItemEntry fromJson(JsonObject jsonObject) {
            int count = jsonObject.has("count") ? jsonObject.get("count").getAsInt() : 1;
            return new ItemEntry(
                    ItemStack.of(Objects.requireNonNull(Material.fromNamespaceId(jsonObject.get("id").getAsString())), count),
                    jsonObject.get("weight").getAsInt()
            );
        }

    }

}
