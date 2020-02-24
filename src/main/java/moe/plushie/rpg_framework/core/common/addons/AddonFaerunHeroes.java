package moe.plushie.rpg_framework.core.common.addons;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Wallet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class AddonFaerunHeroes extends ModAddon {

    private static final String TAG_TIAMATRPG = "tiamatrpg";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_VALUE = "value";

    public AddonFaerunHeroes() {
        super("tiamatrpg", "Faerun Heroes");
    }

    public ICost getItemValue(ItemStack itemStack) {
        ICost value = Cost.NO_COST;
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(TAG_TIAMATRPG, NBT.TAG_COMPOUND)) {
            // Get the tiamatrpg from the item stack.
            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag(TAG_TIAMATRPG);

            // Read value from item NBT.
            if (compound.hasKey(TAG_VALUE, NBT.TAG_INT)) {
                // TODO Make this load from a config or something.
                ICurrency currency = RPGFramework.getProxy().getCurrencyManager().getDefault();
                value = new Cost(new Wallet(currency, compound.getInteger(TAG_VALUE)));
            }
        }
        return value;
    }
}
