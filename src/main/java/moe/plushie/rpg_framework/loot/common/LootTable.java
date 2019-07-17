package moe.plushie.rpg_framework.loot.common;

import java.util.ArrayList;
import java.util.Random;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.loot.ILootTable;
import moe.plushie.rpg_framework.api.loot.ILootTablePool;
import moe.plushie.rpg_framework.core.database.TableLootPools;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class LootTable implements ILootTable {

    private final IIdentifier identifier;
    private final String name;
    private final String category;
    private final ArrayList<IIdentifier> lootPools;

    public LootTable(IIdentifier identifier, String name, String category, ArrayList<IIdentifier> lootPools) {
        this.identifier = identifier;
        this.name = name;
        this.category = category;
        this.lootPools = lootPools;
    }

    public LootTable(IIdentifier identifier, String name, String category) {
        this(identifier, name, category, new ArrayList<IIdentifier>());
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
    public ArrayList<IIdentifier> getLootPools() {
        return lootPools;
    }

    @Override
    public NonNullList<ItemStack> getLoot(Random random) {
        NonNullList<ItemStack> items = NonNullList.<ItemStack>create();
        for (IIdentifier identifier : lootPools) {
            ILootTablePool pool = TableLootPools.get(identifier);
            if (pool != null) {
                pool.getLoot(items, random);
            }
        }
        return items;
    }

    @Override
    public String toString() {
        return "LootTable [identifier=" + identifier + ", name=" + name + ", category=" + category + ", lootPools=" + lootPools + "]";
    }
}
