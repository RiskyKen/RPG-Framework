package moe.plushie.rpgeconomy.core.common.items;

import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.init.ModItems;
import moe.plushie.rpgeconomy.core.common.lib.LibGuiIds;
import moe.plushie.rpgeconomy.core.common.lib.LibItemNames;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.CurrencyManager;
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
                ItemStack stack = getWallet(currency);
                if (!stack.isEmpty()) {
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

    public static ItemStack getWallet(ICurrency currency) {
        if (currency.getCurrencyWalletInfo().getCreateWalletItem()) {
            ItemStack stack = new ItemStack(ModItems.WALLET, 1, 0);
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString(TAG_CURRENCY, currency.getName());
            stack.setTagCompound(compound);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            FMLNetworkHandler.openGui(playerIn, RpgEconomy.getInstance(), LibGuiIds.WALLET, worldIn, 0, 0, 0);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
}
