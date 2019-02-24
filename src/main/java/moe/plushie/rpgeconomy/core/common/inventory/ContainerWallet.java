package moe.plushie.rpgeconomy.core.common.inventory;

import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapability;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerWallet extends ModContainer implements IButtonPress {

    private final EntityPlayer player;
    private final Currency currency;
    private final ICurrencyCapability currencyCap;
    private final IWallet wallet;
    private final ModInventory inventoryWallet;

    public ContainerWallet(EntityPlayer entityPlayer, Currency currency) {
        super(entityPlayer.inventory);

        this.player = entityPlayer;
        this.currency = currency;
        this.currencyCap = CurrencyCapability.get(entityPlayer);
        this.wallet = currencyCap.getWallet(currency);

        inventoryWallet = new ModInventory("wallet", currency.getCurrencyVariants().length);

        addPlayerSlots(8, 114);

        for (int i = 0; i < currency.getCurrencyVariants().length; i++) {
            addSlotToContainer(new SlotCurrency(currency, currency.getCurrencyVariants()[i], inventoryWallet, i, 34 + i * 18, 54));
            inventoryWallet.setInventorySlotContents(i, currency.getCurrencyVariants()[i].getItem().copy());
        }
    }

    @Override
    public void buttonPress(int buttonID) {
        RpgEconomy.getLogger().info("Button ID: " + buttonID);
    }
}
