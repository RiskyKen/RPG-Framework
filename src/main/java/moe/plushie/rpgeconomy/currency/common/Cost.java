package moe.plushie.rpgeconomy.currency.common;

import java.util.Arrays;

import moe.plushie.rpgeconomy.api.core.IItemMatcher;
import moe.plushie.rpgeconomy.api.currency.ICost;
import moe.plushie.rpgeconomy.api.currency.ICurrency;
import moe.plushie.rpgeconomy.api.currency.ICurrency.ICurrencyWalletInfo;
import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.api.currency.IWallet;
import moe.plushie.rpgeconomy.currency.common.capability.CurrencyCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class Cost implements ICost {

    public static final ICost NO_COST = new Cost(null, null);

    private final IWallet walletCost;
    private final IItemMatcher[] itemCost;

    public Cost(IWallet walletCost, IItemMatcher[] itemCost) {
        this.walletCost = walletCost;
        this.itemCost = itemCost;
    }

    @Override
    public IWallet getWalletCost() {
        return walletCost;
    }

    @Override
    public IItemMatcher[] getItemCost() {
        return itemCost;
    }

    @Override
    public boolean hasWalletCost() {
        return walletCost != null;
    }

    @Override
    public boolean hasItemCost() {
        return itemCost != null;
    }

    @Override
    public boolean canAfford(EntityPlayer player) {
        if (hasWalletCost() & !hasItemCost()) {
            ICurrency currency = walletCost.getCurrency();
            if (CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, walletCost.getAmount(), true)) {
                return true;
            }

            ICurrencyWalletInfo walletInfo = currency.getCurrencyWalletInfo();
            if (walletInfo.getNeedItemToAccess()) {
                if (!CurrencyWalletHelper.haveWalletForCurrency(player, currency)) {
                    return false;
                }
            }
            ICurrencyCapability capability = CurrencyCapability.get(player);
            if (capability != null) {
                IWallet playerWallet = capability.getWallet(currency);
                if (playerWallet != null) {

                    if (playerWallet.getAmount() >= walletCost.getAmount()) {
                        return true;
                    }
                }
            }

        }
        if (hasItemCost() & !hasWalletCost()) {
            return CurrencyWalletHelper.payWithItems(player.inventory, itemCost, true);
        }
        if (!hasWalletCost() & !hasItemCost()) {
            return true;
        }
        return false;
    }

    @Override
    public void pay(EntityPlayer player) {
        if (hasWalletCost() & !hasItemCost()) {
            ICurrency currency = walletCost.getCurrency();

            if (CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, walletCost.getAmount(), true)) {
                if (CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, walletCost.getAmount(), false)) {
                    return;
                }
            }

            ICurrencyWalletInfo walletInfo = currency.getCurrencyWalletInfo();
            if (walletInfo.getNeedItemToAccess()) {
                if (!CurrencyWalletHelper.haveWalletForCurrency(player, currency)) {
                    return;
                }
            }
            ICurrencyCapability capability = CurrencyCapability.get(player);
            if (capability != null) {
                IWallet playerWallet = capability.getWallet(currency);
                if (playerWallet != null) {
                    playerWallet.removeAmount(walletCost.getAmount());
                    if (player instanceof EntityPlayerMP) {
                        capability.syncToOwner((EntityPlayerMP) player);
                    }
                }
            }
        }

        if (hasItemCost() & !hasWalletCost()) {
            CurrencyWalletHelper.payWithItems(player.inventory, itemCost, false);
        }
    }

    @Override
    public String toString() {
        return "Cost [walletCost=" + walletCost + ", itemCost=" + Arrays.toString(itemCost) + "]";
    }
}
