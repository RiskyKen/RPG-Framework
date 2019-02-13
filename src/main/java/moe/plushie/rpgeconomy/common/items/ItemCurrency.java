package moe.plushie.rpgeconomy.common.items;

import moe.plushie.rpgeconomy.common.lib.LibItemNames;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemCurrency extends AbstractModItem {

    public ItemCurrency() {
        super(LibItemNames.CURRENCY);
        setHasSubtypes(true);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < 3; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }
}
