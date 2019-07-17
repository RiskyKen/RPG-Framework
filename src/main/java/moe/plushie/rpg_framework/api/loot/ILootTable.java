package moe.plushie.rpg_framework.api.loot;

import java.util.ArrayList;
import java.util.Random;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ILootTable {

    public IIdentifier getIdentifier();

    public String getName();

    public String getCategory();

    public ArrayList<IIdentifier> getLootPools();

    public NonNullList<ItemStack> getLoot(Random random);
}
