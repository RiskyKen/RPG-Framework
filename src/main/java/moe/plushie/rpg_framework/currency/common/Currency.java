package moe.plushie.rpg_framework.currency.common;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.core.IItemMatcher;
import moe.plushie.rpg_framework.api.currency.ICurrency;

public class Currency implements ICurrency, Comparable<ICurrency> {

    private final IIdentifier identifier;

    /** Name of the currency. (this is used as the currency ID) */
    private final String name;

    private final String displayFormat;

    private final CurrencyWalletInfo walletInfo;

    /** Different variants of this currency. */
    private final CurrencyVariant[] variants;

    public Currency(IIdentifier identifier, String name, String displayFormat, CurrencyWalletInfo walletInfo, CurrencyVariant[] variants) {
        this.identifier = identifier;
        this.name = name;
        this.displayFormat = displayFormat;
        this.walletInfo = walletInfo;
        this.variants = variants;
    }

    @Override
    public IIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayFormat() {
        return displayFormat;
    }

    @Override
    public ICurrencyWalletInfo getCurrencyWalletInfo() {
        return walletInfo;
    }

    @Override
    public CurrencyVariant[] getCurrencyVariants() {
        return getCurrencyVariants(true);
    }
    
    @Override
    public CurrencyVariant[] getCurrencyVariants(boolean smallestFirst) {
        if (smallestFirst) {
            return variants;
        } else {
            CurrencyVariant[] variantsClone = variants.clone();
            ArrayUtils.reverse(variantsClone);
            return variantsClone;
        }
    }
    
    @Override
    public int compareTo(ICurrency o) {
        return name.compareTo(o.getName());
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Currency other = (Currency) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Currency [identifier=" + identifier + ", name=" + name + ", displayFormat=" + displayFormat + ", walletInfo=" + walletInfo + ", variants=" + Arrays.toString(variants) + "]";
    }

    public static class CurrencyVariant implements ICurrencyVariant, Comparable<CurrencyVariant> {

        private final String name;
        private final int value;
        private final IItemMatcher item;

        public CurrencyVariant(String name, int value, IItemMatcher item) {
            this.name = name;
            this.value = value;
            this.item = item;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public IItemMatcher getItem() {
            return item;
        }

        @Override
        public String toString() {
            return "Variant [name=" + name + ", value=" + value + ", item=" + item + "]";
        }

        @Override
        public int compareTo(CurrencyVariant o) {
            return value - o.value;
        }
    }

    public static class CurrencyWalletInfo implements ICurrencyWalletInfo {

        /** Will a wallet item be generated for this currency. */
        private final boolean createWalletItem;

        /** Must the player have the wallet item in their inventory to access the wallet GUI. */
        private final boolean needItemToAccess;

        /** Can the wallet GUI be opened with a key binding. */
        private final String modKeybind;

        /** Should picked up items be auto added to the wallet. */
        private final boolean pickupIntoWallet;

        private final float deathPercentageDropped;

        private final float deathPercentageLost;

        public CurrencyWalletInfo(boolean createWalletItem, boolean needItemToAccess, String modKeybind, boolean pickupIntoWallet, float deathPercentageDropped, float deathPercentageLost) {
            this.createWalletItem = createWalletItem;
            this.needItemToAccess = needItemToAccess;
            this.modKeybind = modKeybind;
            this.pickupIntoWallet = pickupIntoWallet;
            this.deathPercentageDropped = deathPercentageDropped;
            this.deathPercentageLost = deathPercentageLost;
        }

        @Override
        public boolean getCreateWalletItem() {
            return createWalletItem;
        }

        @Override
        public boolean getNeedItemToAccess() {
            return needItemToAccess;
        }

        @Override
        public String getModKeybind() {
            return modKeybind;
        }

        @Override
        public boolean getPickupIntoWallet() {
            return pickupIntoWallet;
        }

        @Override
        public float getDeathPercentageDropped() {
            return deathPercentageDropped;
        }

        @Override
        public float getDeathPercentageLost() {
            return deathPercentageLost;
        }
    }
}
