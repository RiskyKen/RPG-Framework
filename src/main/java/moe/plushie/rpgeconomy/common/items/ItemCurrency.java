package moe.plushie.rpgeconomy.common.items;

import moe.plushie.rpgeconomy.common.lib.LibItemNames;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCurrency extends AbstractModItem {

    private static final String[] COIN_TYPES = { "copper", "silver", "gold", "platinum", "emerald", "diamond" };

    public ItemCurrency() {
        super(LibItemNames.CURRENCY);
        setHasSubtypes(true);
        setMaxStackSize(64);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < COIN_TYPES.length; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        for (int i = 0; i < COIN_TYPES.length; i++) {
            ModelLoader.setCustomModelResourceLocation(this, i, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "_" + COIN_TYPES[i]), "normal"));
        }
    }
}
