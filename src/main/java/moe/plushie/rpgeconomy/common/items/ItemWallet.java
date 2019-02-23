package moe.plushie.rpgeconomy.common.items;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.CurrencyManager;
import moe.plushie.rpgeconomy.common.init.ModItems;
import moe.plushie.rpgeconomy.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.common.lib.LibItemNames;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;

public class ItemWallet extends AbstractModItem {

    private static final String TAG_CURRENCY = "currency";
    
    public ItemWallet() {
        super(LibItemNames.WALLET);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
            for (Currency currency : currencyManager.getCurrencies()) {
                if (currency.getHasWallet()) {
                    ItemStack stack = new ItemStack(this, 1, 0);
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setString(TAG_CURRENCY, currency.getName());
                    stack.setTagCompound(compound);
                    items.add(stack);
                }
            }
        }
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Currency currency = getCurrency(stack);
        if (currency != null) {
            return super.getItemStackDisplayName(stack) + " (" + currency.getName() + ")";
        }
        return super.getItemStackDisplayName(stack);
    }
    
    public static Currency getCurrency(ItemStack itemStack) {
        if (itemStack.getItem() == ModItems.WALLET) {
            if (itemStack.hasTagCompound()) {
                if (itemStack.getTagCompound().hasKey(TAG_CURRENCY, NBT.TAG_STRING)) {
                    CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
                    return currencyManager.getCurrency(itemStack.getTagCompound().getString(TAG_CURRENCY));
                }
            }
        }
        return null;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, RpgEconomy.getInstance(), LibGuiIds.WALLET, worldIn, 0, 0, 0);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
}
