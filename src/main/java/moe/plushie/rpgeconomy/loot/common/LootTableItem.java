package moe.plushie.rpgeconomy.loot.common;

import moe.plushie.rpgeconomy.api.loot.ILootTableItem;
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
}
