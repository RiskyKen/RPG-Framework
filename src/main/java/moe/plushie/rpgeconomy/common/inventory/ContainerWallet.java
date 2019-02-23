package moe.plushie.rpgeconomy.common.inventory;

import moe.plushie.rpgeconomy.common.capability.currency.CurrencyCapability;
import moe.plushie.rpgeconomy.common.capability.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.Currency.Variant;
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
        }
    }
    
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        // TODO Auto-generated method stub
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public void setInventorySlotContents(IInventory inventory, int index, ItemStack stack) {
        if (!stack.isEmpty()) {
            for (Variant variant : currency.getVariants()) {
                if (stack.isItemEqualIgnoreDurability(variant.getItem())) {
                    wallet.setAmount(variant.getValue() * stack.getCount());
                    if (!player.getEntityWorld().isRemote) {
                        currencyCap.syncToOwner((EntityPlayerMP) player);
                    }
                    break;
                }
            }
        }
        //RpgEconomy.getLogger().info(stack);
    }

    @Override
    public void dirty() {
        
    }
}
