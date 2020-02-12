package moe.plushie.rpg_framework.core.common.addons;

import java.util.ArrayList;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Wallet;
import moe.plushie.rpg_framework.itemData.ItemData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public class AddonFaerunHeroes extends ModAddon {

    private static final String TAG_TIAMATRPG = "tiamatrpg";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_VALUE = "value";

    public AddonFaerunHeroes() {
        super("tiamatrpg", "Faerun Heroes");
    }

    public IItemData getItemData(ItemStack itemStack) {
        ItemData itemData = ItemData.ITEM_DATA_MISSING;
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(TAG_TIAMATRPG, NBT.TAG_COMPOUND)) {
            // Get the tiamatrpg from the item stack.
            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag(TAG_TIAMATRPG);

            ArrayList<String> categories = new ArrayList<String>();
            ICost value = Cost.NO_COST;

            // Read categories from item NBT.
            if (compound.hasKey(TAG_CATEGORIES, NBT.TAG_LIST)) {
                NBTTagList categoriesList = compound.getTagList(TAG_CATEGORIES, NBT.TAG_STRING);
                for (int i = 0; i < categoriesList.tagCount(); i++) {
                    categories.add(categoriesList.getStringTagAt(i));
                }
            }

            // Read value from item NBT.
            if (compound.hasKey(TAG_VALUE, NBT.TAG_INT)) {
                // TODO Make this load from a config or something.
                ICurrency currency = RPGFramework.getProxy().getCurrencyManager().getDefault();
                value = new Cost(new Wallet(currency, compound.getInteger(TAG_VALUE)), null);
            }

            itemData = ItemData.create(categories, new ArrayList<String>(), value);
        }
        return itemData;
    }

    public void setItemData(ItemStack itemStack, IItemData itemData) {
        if (itemData != null || itemData != ItemData.ITEM_DATA_MISSING) {
            // Create tag compound if needed.
            if (!itemStack.hasTagCompound()) {
                itemStack.setTagCompound(new NBTTagCompound());
            }

            // Create tiamatrpg tag compound if needed.
            if (!itemStack.getTagCompound().hasKey(TAG_TIAMATRPG, NBT.TAG_COMPOUND)) {
                itemStack.getTagCompound().setTag(TAG_TIAMATRPG, new NBTTagCompound());
            }

            // Get the tiamatrpg from the item stack.
            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag(TAG_TIAMATRPG);

            // Write value to item NBT.
            if (!itemData.getCategories().isEmpty()) {
                NBTTagList categoriesList = new NBTTagList();
                for (String category : itemData.getCategories()) {
                    categoriesList.appendTag(new NBTTagString(category));
                }
            }

            // Write value to item NBT.
            if (itemData.getValue() != Cost.NO_COST) {
                compound.setInteger(TAG_VALUE, itemData.getValue().getWalletCost().getAmount());
            }
        }
    }
}
