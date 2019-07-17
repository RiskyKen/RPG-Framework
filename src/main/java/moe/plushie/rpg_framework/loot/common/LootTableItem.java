package moe.plushie.rpg_framework.loot.common;

import moe.plushie.rpg_framework.api.loot.ILootTableItem;
import net.minecraft.item.ItemStack;

public class LootTableItem implements ILootTableItem {

    private final ItemStack item;
    private final int weight;

    public LootTableItem(ItemStack item, int weight) {
        this.item = item;
        this.weight = weight;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "LootTableItem [item=" + item + ", weight=" + weight + "]";
    }
}
