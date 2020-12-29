package moe.plushie.rpg_framework.currency.common;

import java.util.ArrayList;
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

    public static final ICost NO_COST = CostFactory.newCost().build();

    private final IWallet[] walletCosts;
    private final IItemMatcher[] itemCosts;
    private final IItemMatcher[] oreDictionaryCosts;
    private final IItemMatcher[] itemValueCosts;

    public Cost(IWallet[] walletCosts, IItemMatcher[] itemCosts, IItemMatcher[] oreDictionaryCosts, IItemMatcher[] itemValueCosts) {
        this.walletCosts = removeDupes(walletCosts);
        this.itemCosts = removeNulls(itemCosts);
        this.oreDictionaryCosts = removeNulls(oreDictionaryCosts);
        this.itemValueCosts = removeNulls(itemValueCosts);
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
    public IItemMatcher[] getOreDictionaryCosts() {
        return oreDictionaryCosts;
    }

    @Override
    public IItemMatcher[] getItemValueCosts() {
        return itemValueCosts;
    }

    @Override
    public boolean hasWalletCost() {
        if (walletCosts != null && walletCosts.length > 0) {
            return true;
        }
        return walletCosts != null;
    }

    @Override
    public boolean hasItemCost() {
        if (itemCosts != null && itemCosts.length > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasOreDictionaryCost() {
        if (oreDictionaryCosts != null && oreDictionaryCosts.length > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasItemValueCosts() {
        if (itemValueCosts != null && itemValueCosts.length > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canAfford(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }

        if (hasWalletCost()) {
            for (IWallet cost : walletCosts) {
                ICurrency costCurrency = cost.getCurrency();
                if (CurrencyWalletHelper.consumeAmountFromInventory(costCurrency, player.inventory, cost.getAmount(), true)) {
                    continue;
                }

                ICurrencyWalletInfo walletInfo = costCurrency.getCurrencyWalletInfo();
                if (walletInfo.getNeedItemToAccess()) {
                    if (!CurrencyWalletHelper.haveWalletForCurrency(player, costCurrency)) {
                        return false;
                    }
                }
                ICurrencyCapability capability = CurrencyCapability.get(player);
                if (capability != null) {
                    IWallet playerWallet = capability.getWallet(costCurrency);
                    if (playerWallet != null) {
                        if (playerWallet.getAmount() < cost.getAmount()) {
                            return false;
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

        if (hasOreDictionaryCost()) {
            throw new NotImplementedException("Paying with OreDictionaryCosts is not implemented yet.");
        }

        if (hasItemValueCosts()) {
            throw new NotImplementedException("Paying with ItemValueCosts is not implemented yet.");
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
                            capability.syncToOwner((EntityPlayerMP) player, true);
                        }
                    }
                }
            }
        }

        if (hasItemCost()) {
            CurrencyWalletHelper.payWithItems(player.inventory, itemCosts, false);
        }

        if (hasOreDictionaryCost()) {
            throw new NotImplementedException("Paying with OreDictionaryCosts is not implemented yet.");
        }

        if (hasItemValueCosts()) {
            throw new NotImplementedException("Paying with ItemValueCosts is not implemented yet.");
        }
    }

    @Override
    public void refund(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        if (hasWalletCost()) {
            for (IWallet wallet : walletCosts) {
                ICurrency currency = wallet.getCurrency();
                int amount = CurrencyWalletHelper.addAmountToWallet(currency, player, wallet.getAmount());
                if (amount > 0) {
                    CurrencyWalletHelper.addAmountToInventory(currency, player, wallet.getAmount(), false);
                }
            }
        }
        if (hasItemCost()) {
            throw new NotImplementedException("Refunding with Items is not implemented yet.");
            // CurrencyWalletHelper.refundItems(player.inventory, itemCosts, false);
        }
        if (hasOreDictionaryCost()) {
            throw new NotImplementedException("Refunding with OreDictionaryCosts is not implemented yet.");
        }

        if (hasItemValueCosts()) {
            throw new NotImplementedException("Refunding with ItemValueCosts is not implemented yet.");
        }
    }

    @Override
    public String toString() {
        return "Cost [walletCosts=" + Arrays.toString(walletCosts) + ", itemCosts=" + Arrays.toString(itemCosts) + ", oreDictionaryCosts=" + Arrays.toString(oreDictionaryCosts) + ", itemValueCosts=" + Arrays.toString(itemValueCosts) + "]";
    }

    @Override
    public ICost add(ICost... costs) {
        return CostFactory.newCost().addCost(costs).addCost(this).build();
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

    private static IItemMatcher[] removeNulls(IItemMatcher[] inItemMatchers) {
        if (inItemMatchers != null && inItemMatchers.length > 0) {
            ArrayList<IItemMatcher> itemMatchers = new ArrayList<IItemMatcher>();
            for (IItemMatcher matcher : inItemMatchers) {
                if (matcher != null && !matcher.getItemStack().isEmpty()) {
                    itemMatchers.add(matcher);
                }
            }
            return itemMatchers.toArray(new IItemMatcher[itemMatchers.size()]);
        }
        return inItemMatchers;
    }

    @Override
    public boolean isNoCost() {
        if (this == NO_COST) {
            return true;
        }
        if (!this.hasWalletCost() & !this.hasItemCost() & !this.hasOreDictionaryCost() & !this.hasItemValueCosts()) {
            return true;
        }
        return false;
    }

    public static final class CostFactory {

        private final ArrayList<IWallet> walletCosts = new ArrayList<IWallet>();
        private final ArrayList<IItemMatcher> itemCosts = new ArrayList<IItemMatcher>();
        private final ArrayList<IItemMatcher> oreDictionaryCosts = new ArrayList<IItemMatcher>();
        private final ArrayList<IItemMatcher> itemValueCosts = new ArrayList<IItemMatcher>();

        private CostFactory() {
        }

        public static CostFactory newCost() {
            return new CostFactory();
        }

        public CostFactory addWalletCosts(IWallet... walletCosts) {
            this.walletCosts.addAll(Arrays.asList(walletCosts));
            return this;
        }

        public CostFactory addItemCosts(IItemMatcher... itemCosts) {
            this.itemCosts.addAll(Arrays.asList(itemCosts));
            return this;
        }

        public CostFactory addOreDictionaryCosts(IItemMatcher... oreDictionaryCosts) {
            this.oreDictionaryCosts.addAll(Arrays.asList(oreDictionaryCosts));
            return this;
        }

        public CostFactory addItemValueCosts(IItemMatcher... itemValueCosts) {
            this.itemValueCosts.addAll(Arrays.asList(itemValueCosts));
            return this;
        }

        public CostFactory addCost(ICost... costs) {
            for (ICost cost : costs) {
                if (cost.hasWalletCost()) {
                    addWalletCosts(cost.getWalletCosts());
                }
                if (cost.hasItemCost()) {
                    addItemCosts(cost.getItemCosts());
                }
                if (cost.hasOreDictionaryCost()) {
                    addOreDictionaryCosts(cost.getOreDictionaryCosts());
                }
                if (cost.hasItemValueCosts()) {
                    addItemValueCosts(cost.getItemValueCosts());
                }
            }
            return this;
        }

        public ICost build() {
            IWallet[] walletCosts = null;
            IItemMatcher[] itemCosts = null;
            IItemMatcher[] oreDictionaryCosts = null;
            IItemMatcher[] itemValueCosts = null;

            if (!this.walletCosts.isEmpty()) {
                walletCosts = this.walletCosts.toArray(new IWallet[this.walletCosts.size()]);
            }
            if (!this.itemCosts.isEmpty()) {
                itemCosts = this.itemCosts.toArray(new IItemMatcher[this.itemCosts.size()]);
            }
            if (!this.oreDictionaryCosts.isEmpty()) {
                oreDictionaryCosts = this.oreDictionaryCosts.toArray(new IItemMatcher[this.oreDictionaryCosts.size()]);
            }
            if (!this.itemValueCosts.isEmpty()) {
                itemValueCosts = this.itemValueCosts.toArray(new IItemMatcher[this.itemValueCosts.size()]);
            }

            return new Cost(walletCosts, itemCosts, oreDictionaryCosts, itemValueCosts);
        }
    }
}
