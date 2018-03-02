package moe.plushie.rpgeconomy.common.creativetab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moe.plushie.rpgeconomy.common.lib.LibModInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class CreativeTabRPGEconomy extends CreativeTabs {

    public CreativeTabRPGEconomy() {
        super(LibModInfo.ID);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Item getTabIconItem() {
        return Item.getItemFromBlock(Blocks.dirt);
    }
}
