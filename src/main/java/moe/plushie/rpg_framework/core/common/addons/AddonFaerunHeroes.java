package moe.plushie.rpg_framework.core.common.addons;

import java.io.File;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.currency.common.Cost;
import moe.plushie.rpg_framework.currency.common.Cost.CostFactory;
import moe.plushie.rpg_framework.currency.common.Wallet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants.NBT;

public class AddonFaerunHeroes extends ModAddon {

    private static final String TAG_TIAMATRPG = "tiamatrpg";
    private static final String TAG_CATEGORIES = "categories";
    private static final String TAG_VALUE = "value";

    public static final String CONFIG_CATEGORY_GENERAL = "General";
    private Configuration config;

    private IdentifierString faerunCurrencyIdentifier = null;

    public AddonFaerunHeroes() {
        super("faeruncharacters", "Faerun Heroes");
    }

    @Override
    protected boolean setIsModLoaded() {
        // return true;
        return super.setIsModLoaded();
    }

    @Override
    public void preInit() {
        if (config == null) {
            config = new Configuration(getConfigFile(), "1");
            loadConfigFile();
        }
    }

    private File getConfigFile() {
        return new File(RPGFramework.getProxy().getModConfigDirectory(), "faerun_heroes.cfg");
    }

    private void loadConfigFile() {
        loadCategoryGeneral();
        if (config.hasChanged()) {
            config.save();
        }
    }

    private void loadCategoryGeneral() {
        config.setCategoryComment(CONFIG_CATEGORY_GENERAL, "General settings.");

        String currencyIdentifier = config.getString("faerunCurrencyIdentifier", CONFIG_CATEGORY_GENERAL, "money.json", "The currency that should be used for tiamatrpg values.");
        faerunCurrencyIdentifier = new IdentifierString(currencyIdentifier);
    }

    private ICurrency getFaerunCurrency() {
        ICurrency currency = null;
        if (faerunCurrencyIdentifier != null) {
            currency = RPGFramework.getProxy().getCurrencyManager().getCurrency(faerunCurrencyIdentifier);
        }
        if (currency == null) {
            currency = RPGFramework.getProxy().getCurrencyManager().getDefault();
        }
        return currency;
    }

    public ICost getItemValue(ItemStack itemStack) {
        ICost value = Cost.NO_COST;
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey(TAG_TIAMATRPG, NBT.TAG_COMPOUND)) {
            // Get the tiamatrpg from the item stack.
            NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag(TAG_TIAMATRPG);

            // Read value from item NBT.
            if (compound.hasKey(TAG_VALUE, NBT.TAG_INT)) {
                // TODO Make this load from a config or something.
                ICurrency currency = getFaerunCurrency();
                Wallet itemValue = new Wallet(currency, compound.getInteger(TAG_VALUE));
                value = CostFactory.newCost().addWalletCosts(itemValue).build();
            }
        }
        return value;
    }
}
