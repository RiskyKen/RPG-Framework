package moe.plushie.rpg_framework.currency.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.lang3.ArrayUtils;

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

    private final IWallet[] walletCosts;
    private final IItemMatcher[] itemCosts;

    public Cost(IWallet... walletCosts) {
        this(walletCosts, null);
    }

    public Cost(IItemMatcher... itemCosts) {
        this(null, itemCosts);
    }

    public Cost(IWallet[] walletCosts, IItemMatcher[] itemCosts) {
        this.walletCosts = removeDupes(walletCosts);
        this.itemCosts = itemCosts;
    }

    @Override
    public IWallet[] getWalletCosts() {
        return walletCosts;
    }

    @Override
    public IItemMatcher[] getItemCosts() {
        return itemCosts;
    }

    @Override
    public boolean hasWalletCost() {
        if (walletCosts != null) {
            if (walletCosts.length > 0) {
                return true;
            }
        }
        return walletCosts != null;
    }

    @Override
    public boolean hasItemCost() {
        if (itemCosts != null) {
            if (itemCosts.length > 0) {
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

        if (hasWalletCost()) {
            for (IWallet wallet : walletCosts) {
                ICurrency currency = wallet.getCurrency();
                if (CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, wallet.getAmount(), true)) {
                    continue;
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
                        if (playerWallet.getAmount() >= wallet.getAmount()) {
                            continue;
                        }
                    }
                }
            }
        }
        if (hasItemCost()) {
            if (!CurrencyWalletHelper.payWithItems(player.inventory, itemCosts, true)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void pay(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        if (hasWalletCost()) {
            for (IWallet wallet : walletCosts) {
                ICurrency currency = wallet.getCurrency();

                if (CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, wallet.getAmount(), true)) {
                    if (CurrencyWalletHelper.consumeAmountFromInventory(currency, player.inventory, wallet.getAmount(), false)) {
                        continue;
                    }
                }

                ICurrencyWalletInfo walletInfo = currency.getCurrencyWalletInfo();
                if (walletInfo.getNeedItemToAccess()) {
                    if (!CurrencyWalletHelper.haveWalletForCurrency(player, currency)) {
                        // continue;
                    }
                }
                ICurrencyCapability capability = CurrencyCapability.get(player);
                if (capability != null) {
                    IWallet playerWallet = capability.getWallet(currency);
                    if (playerWallet != null) {
                        playerWallet.removeAmount(wallet.getAmount());
                        if (player instanceof EntityPlayerMP) {
                            capability.syncToOwner((EntityPlayerMP) player);
                        }
                    }
                }
            }
        }

        if (hasItemCost()) {
            CurrencyWalletHelper.payWithItems(player.inventory, itemCosts, false);
        }
    }

    @Override
    public String toString() {
        return "Cost [walletCosts=" + Arrays.toString(walletCosts) + ", itemCosts=" + Arrays.toString(itemCosts) + "]";
    }

    @Override
    public ICost add(ICost... costs) {
        costs = ArrayUtils.addAll(costs, this);
        ArrayList<IItemMatcher> costItems = new ArrayList<IItemMatcher>();
        ArrayList<IWallet> costWallets = new ArrayList<IWallet>();
        for (ICost cost : costs) {
            if (cost.hasItemCost()) {
                Collections.addAll(costItems, cost.getItemCosts());
            }
            if (cost.hasWalletCost()) {
                Collections.addAll(costWallets, cost.getWalletCosts());
            }
        }

        if (costWallets.isEmpty() & costItems.isEmpty()) {
            return NO_COST;
        }

        IWallet[] wallets = costWallets.toArray(new IWallet[costWallets.size()]);
        IItemMatcher[] itemMatchers = costItems.toArray(new IItemMatcher[costItems.size()]);

        return new Cost(wallets, itemMatchers);
    }

    private static IWallet[] removeDupes(IWallet[] walletsIn) {
        if (walletsIn != null && walletsIn.length > 0) {
            ArrayList<ICurrency> currencies = new ArrayList<ICurrency>();
            for (IWallet wallet : walletsIn) {
                if (wallet != null) {
                    if (!currencies.contains(wallet.getCurrency())) {
                        currencies.add(wallet.getCurrency());
                    }
                }
            }

            ArrayList<IWallet> newWalletList = new ArrayList<IWallet>();
            for (ICurrency currency : currencies) {
                IWallet newWallet = new Wallet(currency);
                for (IWallet wallet : walletsIn) {
                    if (wallet != null) {
                        if (wallet.getCurrency() == currency) {
                            newWallet.addAmount(wallet.getAmount());
                        }
                    }
                }
                newWalletList.add(newWallet);
            }
            return newWalletList.toArray(new IWallet[newWalletList.size()]);
        }
        return walletsIn;
    }

    @Override
    public boolean isNoCost() {
        if (this == NO_COST) {
            return true;
        }
        if (!this.hasWalletCost() & !this.hasItemCost()) {
            return true;
        }
        return false;
    }
}
