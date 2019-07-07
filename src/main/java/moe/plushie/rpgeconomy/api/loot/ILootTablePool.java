package moe.plushie.rpgeconomy.api.loot;

import java.util.ArrayList;
import java.util.Random;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ILootTablePool {

    public IIdentifier getIdentifier();

    public String getName();

    public String getCategory();

    public ArrayList<ILootTableItem> getPoolItems();

    public void getLoot(NonNullList<ItemStack> items, Random random);
}
