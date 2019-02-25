package moe.plushie.rpgeconomy.currency.common.inventory;

import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.core.common.config.ConfigHandler;
import moe.plushie.rpgeconomy.core.common.inventory.ModContainer;
import moe.plushie.rpgeconomy.core.common.inventory.ModInventory;
import moe.plushie.rpgeconomy.core.common.inventory.slot.SlotCurrency;
import moe.plushie.rpgeconomy.core.common.network.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.rpgeconomy.currency.common.Currency;
import moe.plushie.rpgeconomy.currency.common.Currency.CurrencyVariant;
import moe.plushie.rpgeconomy.currency.common.CurrencyHelper;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

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

        if (ConfigHandler.showPlayerInventoryInWalletGUI) {
            addPlayerSlots(8, 114);
        }

        for (int i = 0; i < currency.getCurrencyVariants().length; i++) {
            addSlotToContainer(new SlotCurrency(currency, currency.getCurrencyVariants()[i], inventoryWallet, i, 34 + i * 18, 54));
            inventoryWallet.setInventorySlotContents(i, currency.getCurrencyVariants()[i].getItem().copy());
        }
    }

    @Override
    public void buttonPress(int buttonID) {
        boolean withdraw = false;
        if (buttonID >= currency.getCurrencyVariants().length) {
            withdraw = true;
        }
        buttonID = buttonID % currency.getCurrencyVariants().length;
        CurrencyVariant variant = currency.getCurrencyVariants()[buttonID];
        if (withdraw) {
            if (wallet.getAmount() >= variant.getValue()) {
                if (player.addItemStackToInventory(variant.getItem().copy())) {
                    wallet.setAmount(wallet.getAmount() - variant.getValue());
                    currencyCap.syncToOwner((EntityPlayerMP) player);
                }
            }
        } else {
            if (CurrencyHelper.consumeAmountFromInventory(currency, player.inventory, variant.getValue(), true)) {
                CurrencyHelper.consumeAmountFromInventory(currency, player.inventory, variant.getValue(), false);
                wallet.setAmount(wallet.getAmount() + variant.getValue());
                currencyCap.syncToOwner((EntityPlayerMP) player);
            }
        }
    }
}
