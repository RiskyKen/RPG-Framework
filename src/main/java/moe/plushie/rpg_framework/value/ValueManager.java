package moe.plushie.rpg_framework.value;

import java.io.File;

import moe.plushie.rpg_framework.api.currency.ICost;
import moe.plushie.rpg_framework.api.value.IValueManager;
import moe.plushie.rpg_framework.core.common.addons.ModAddonManager;
import moe.plushie.rpg_framework.currency.common.Cost;
import net.minecraft.item.ItemStack;

public final class ValueManager implements IValueManager {

    private static final String DIRECTORY_NAME = "shop";

    private final File valueDirectory;

    public ValueManager(File modDirectory) {
        valueDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!valueDirectory.exists()) {
            valueDirectory.mkdir();
        }
    }

    public void reload() {
    }

    @Override
    public ICost getValue(ItemStack itemStack) {
        if (ModAddonManager.addonFaerunHeroes.isModLoaded()) {
            return ModAddonManager.addonFaerunHeroes.getValue(itemStack);
        }
        return Cost.NO_COST;
    }

    @Override
    public void setValue(ItemStack itemStack, ICost value) {
        if (value == null) {
            value = Cost.NO_COST;
        }
        if (ModAddonManager.addonFaerunHeroes.isModLoaded()) {
            ModAddonManager.addonFaerunHeroes.setValue(itemStack, value);
        }
    }
}
