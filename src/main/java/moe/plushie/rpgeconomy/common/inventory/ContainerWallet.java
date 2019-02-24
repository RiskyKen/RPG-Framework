package moe.plushie.rpgeconomy.common.inventory;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.common.capability.currency.CurrencyCapability;
import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.Currency.CurrencyVariant;
import moe.plushie.rpgeconomy.common.currency.Wallet;
import moe.plushie.rpgeconomy.common.inventory.ModInventory.IInventoryCallback;
import moe.plushie.rpgeconomy.common.inventory.slot.SlotCurrency;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ContainerWallet extends ModContainer implements IInventoryCallback {

    private final EntityPlayer player;
    private final Currency currency;
    private final ICurrencyCapability currencyCap;
    private final Wallet wallet;
    private final ModInventory inventoryWallet;
    
    public ContainerWallet(EntityPlayer entityPlayer, Currency currency) {
        super(entityPlayer.inventory);
        
        this.player = entityPlayer;
        this.currency = currency;
        this.currencyCap = CurrencyCapability.get(entityPlayer);
        this.wallet = currencyCap.getWallet(currency);
        
        inventoryWallet = new ModInventory("wallet", currency.getVariants().length, this);
        
        
        addPlayerSlots(8, 86);
        
        for (int i = 0; i < currency.getVariants().length; i++) {
            addSlotToContainer(new SlotCurrency(currency, currency.getVariants()[i], inventoryWallet, i, 34 + i * 18, 40));
            inventoryWallet.setInventorySlotContents(i, currency.getVariants()[i].getItem().copy());
        }
    }
    
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        RpgEconomy.getLogger().info(slotId + " " + clickTypeIn);
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int index, ItemStack stack) {
        CurrencyVariant variant = currency.getVariants()[index];
        int value = wallet.getAmount();
        if (stack.isEmpty()) {
            value -= variant.getValue();
            inventory.setInventorySlotContents(index, variant.getItem().copy());
        } 
        
        if (stack.getCount() > 1) {
            value += variant.getValue() * (stack.getCount() - 1);
            inventory.setInventorySlotContents(index, variant.getItem().copy());
        }
        
        wallet.setAmount(value);
        if (!player.getEntityWorld().isRemote) {
            currencyCap.syncToOwner((EntityPlayerMP) player);
        }
        
        /*if (!stack.isEmpty()) {
            for (Variant variant : currency.getVariants()) {
                if (stack.isItemEqualIgnoreDurability(variant.getItem())) {
                    

                    break;
                }
            }
        }*/
        RpgEconomy.getLogger().info(value);
    }

    @Override
    public void dirty() {
        
    }
}
