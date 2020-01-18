package moe.plushie.rpg_framework.currency.common;

import java.util.Arrays;

import org.apache.commons.lang3.NotImplementedException;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrency.ICurrencyWalletInfo;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.currency.common.capability.CurrencyCapability;
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
        if (itemCost != null) {
            if (itemCost.length > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canAfford(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }
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
        if (player.capabilities.isCreativeMode) {
            return;
        }
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

    @Override
    public ICost add(ICost... costs) {
        ICost returnCost = NO_COST;

        Wallet wallet = null;

        if (hasWalletCost()) {
            if (wallet == null) {
                wallet = new Wallet(getWalletCost().getCurrency());
            }
            if (wallet != null) {
                if (!wallet.getCurrency().getIdentifier().equals(getWalletCost().getCurrency().getIdentifier())) {
                    throw new NotImplementedException("Currency types do not match.");
                }
                wallet.addAmount(getWalletCost().getAmount());
            }
        }
        if (hasItemCost()) {
            throw new NotImplementedException("Can not add item costs at this time.");
        }

        for (ICost cost : costs) {
            if (cost.hasWalletCost()) {
                if (wallet == null) {
                    wallet = new Wallet(cost.getWalletCost().getCurrency());
                }
                if (wallet != null) {
                    if (!wallet.getCurrency().getIdentifier().equals(cost.getWalletCost().getCurrency().getIdentifier())) {
                        throw new NotImplementedException("Currency types do not match.");
                    }
                    wallet.addAmount(cost.getWalletCost().getAmount());
                }
            }
            if (cost.hasItemCost()) {
                throw new NotImplementedException("Can not add item costs at this time.");
            }
        }

        if (wallet != null) {
            returnCost = new Cost(wallet, null);
        }

        return returnCost;
    }
}
