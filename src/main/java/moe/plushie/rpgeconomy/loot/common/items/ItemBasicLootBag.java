package moe.plushie.rpgeconomy.loot.common.items;

import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.items.AbstractModItem;
import moe.plushie.rpgeconomy.core.common.lib.EnumGuiId;
import moe.plushie.rpgeconomy.core.common.lib.LibItemNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class ItemBasicLootBag extends AbstractModItem {

    public ItemBasicLootBag() {
        super(LibItemNames.BASIC_LOOT_BAG);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, RpgEconomy.getInstance(), EnumGuiId.BASIC_LOOT_BAG.ordinal(), worldIn, 0, 0,0);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
}
