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
    private static final String TAG_VALUE = "value";

    public AddonFaerunHeroes() {
        super("tiamatrpg", "Faerun Heroes");
    }

    public ICost getValue(ItemStack itemStack) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(TAG_TIAMATRPG, NBT.TAG_COMPOUND)) {
            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag(TAG_TIAMATRPG);
            if (compound.hasKey(TAG_TIAMATRPG, NBT.TAG_INT)) {
                ICurrency currency = RPGFramework.getProxy().getCurrencyManager().getCurrency("common.json");
                return new Cost(new Wallet(currency, compound.getInteger(TAG_VALUE)), null);
            }
        }
        return Cost.NO_COST;
    }

    public void setValue(ItemStack itemStack, ICost value) {
        if (value != null && value.hasWalletCost()) {
            if (!itemStack.hasTagCompound()) {
                itemStack.setTagCompound(new NBTTagCompound());
            }
            if (!itemStack.getTagCompound().hasKey(TAG_TIAMATRPG, NBT.TAG_COMPOUND)) {
                itemStack.getTagCompound().setTag(TAG_TIAMATRPG, new NBTTagCompound());
            }
            itemStack.getTagCompound().getCompoundTag(TAG_TIAMATRPG).setInteger(TAG_TIAMATRPG, value.getWalletCost().getAmount());
        }
    }
}
