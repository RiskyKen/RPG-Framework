package moe.plushie.rpg_framework.api.currency;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.core.IItemMatcher;

public interface ICurrency {

    public IIdentifier getIdentifier();

    /** Name of the currency. (this is used as the currency ID) */
    public String getName();

    public String getDisplayFormat();

    public ICurrencyWalletInfo getCurrencyWalletInfo();

    /** Different variants of this currency. */
    public ICurrencyVariant[] getCurrencyVariants();

    public static interface ICurrencyVariant {

        public String getName();

        public int getValue();

        public IItemMatcher getItem();
    }

    public static interface ICurrencyWalletInfo {

        /** Will a wallet item be generated for this currency. */
        public boolean getCreateWalletItem();

        /** Must the player have the wallet item in their inventory to open the wallet GUI. */
        public boolean getNeedItemToAccess();

        /** Key binding to open the wallet GUI. */
        public String getModKeybind();

        public boolean getPickupIntoWallet();

        public float getDeathPercentageDropped();

        public float getDeathPercentageLost();
    }
}
