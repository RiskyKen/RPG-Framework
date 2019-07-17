package moe.plushie.rpg_framework.loot.common;

import java.util.ArrayList;
import java.util.Random;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.loot.ILootTableItem;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class LootTablePool implements ILootTablePool {

    private final IIdentifier identifier;
    private final String name;
    private final String category;
    private final ArrayList<ILootTableItem> poolItems;

    public LootTablePool(IIdentifier identifier, String name, String category, ArrayList<ILootTableItem> poolItems) {
        this.identifier = identifier;
        this.name = name;
        this.category = category;
        this.poolItems = poolItems;
    }
    
    public LootTablePool(IIdentifier identifier, String name, String category) {
        this(identifier, name, category, new ArrayList<ILootTableItem>());
    }

    @Override
    public IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public ArrayList<ILootTableItem> getPoolItems() {
        return poolItems;
    }

    @Override
    public void getLoot(NonNullList<ItemStack> items, Random random) {
        double select = random.nextFloat() * (double) getTotalWeight();
        double count = 0;
        for (ILootTableItem poolItem : poolItems) {
            int weight = poolItem.getWeight();
            if (select >= count & select <= count + weight) {
                items.add(poolItem.getItem().copy());
                return;
            }
            count += weight;
        }
        items.add(poolItems.get(poolItems.size()).getItem().copy());
    }

    private int getTotalWeight() {
        int total = 0;
        for (ILootTableItem poolItem : poolItems) {
            total += poolItem.getWeight();
        }
        return total;
    }

    @Override
    public String toString() {
        return "LootTablePool [identifier=" + identifier + ", name=" + name + ", category=" + category + ", poolItems=" + poolItems + "]";
    }
}
