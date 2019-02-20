package moe.plushie.rpgeconomy.common.items;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.common.lib.LibItemNames;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class ItemWallet extends AbstractModItem {

    public ItemWallet() {
        super(LibItemNames.WALLET);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, RpgEconomy.getInstance(), LibGuiIds.WALLET, worldIn, 0, 0, 0);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
}
