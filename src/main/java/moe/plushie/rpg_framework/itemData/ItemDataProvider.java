package moe.plushie.rpg_framework.itemData;

import moe.plushie.rpg_framework.api.itemData.IItemData;
import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ItemDataProvider {

    public static IItemData getItemData(ItemStack itemStack) {
        IItemData itemData = ItemData.ITEM_DATA_MISSING;

        if (RPGFramework.isDedicated()) {
            itemData = getItemDataServer(itemStack);
        } else {
            Side side = FMLCommonHandler.instance().getEffectiveSide();
            switch (side) {
            case SERVER:
                itemData = getItemDataServer(itemStack);
                break;
            case CLIENT:
                itemData = getItemDataClient(itemStack);
                break;
            }
        }

        return itemData;
    }

    private static IItemData getItemDataServer(ItemStack itemStack) {
        return ModuleItemData.getManager().getItemData(itemStack);
    }

    @SideOnly(Side.CLIENT)
    private static IItemData getItemDataClient(ItemStack itemStack) {
        return ItemData.ITEM_DATA_MISSING;
    }
}
