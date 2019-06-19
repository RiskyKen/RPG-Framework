package moe.plushie.rpgeconomy.api;

import moe.plushie.rpgeconomy.api.currency.ICurrencyManager;
import moe.plushie.rpgeconomy.api.mail.IMailSystemManager;
import moe.plushie.rpgeconomy.api.shop.IShopManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public final class RpgEconomyAPI {

    private static final String MOD_ID = "rpg_economy";

    private static ICurrencyManager currencyManager;
    private static IMailSystemManager mailSystemManager;
    private static IShopManager shopManager;

    private RpgEconomyAPI() {
    }

    public static boolean isAvailable() {
        return Loader.isModLoaded(MOD_ID);
    }

    public static ICurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public static IMailSystemManager getMailSystemManager() {
        return mailSystemManager;
    }

    public static IShopManager getShopManager() {
        return shopManager;
    }
}
