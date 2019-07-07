package moe.plushie.rpgeconomy.loot.common;

import java.util.ArrayList;
import java.util.Random;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.api.loot.ILootTable;
import moe.plushie.rpgeconomy.api.loot.ILootTablePool;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class LootTable implements ILootTable {

    private final IIdentifier identifier;
    private final String name;
    private final String category;
    private final ArrayList<ILootTablePool> lootPools;

    public LootTable(IIdentifier identifier, String name, String category, ArrayList<ILootTablePool> lootPools) {
        this.identifier = identifier;
        this.name = name;
        this.category = category;
        this.lootPools = lootPools;
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
    public ArrayList<ILootTablePool> getLootPools() {
        return lootPools;
    }

    @Override
    public NonNullList<ItemStack> getLoot(Random random) {
        NonNullList<ItemStack> items = NonNullList.<ItemStack>create();
        for (ILootTablePool pool : lootPools) {
            pool.getLoot(items, random);
        }
        return items;
    }
}
