package moe.plushie.rpg_framework.core.common.addons;

import java.util.ArrayList;

import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModAddonManager {

    private static final ArrayList<ModAddon> LOADED_ADDONS = new ArrayList<ModAddon>(); 
    
    public static AddonFaerunHeroes addonFaerunHeroes;
    
    private ModAddonManager() {
    }
    
    public static void preInit() {
        loadAddons();
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.preInit();
        }
    }
    
    private static void loadAddons() {
        RPGFramework.getLogger().info("Loading addons");
        addonFaerunHeroes = new AddonFaerunHeroes();
    }
    
    public static void init() {
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.init();
        }
    }
    
    public static void postInit() {
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.postInit();
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static void initRenderers() {
        for (ModAddon modAddon : LOADED_ADDONS) {
            modAddon.initRenderers();
        }
    }
    
    public static ArrayList<ModAddon> getLoadedAddons() {
        return LOADED_ADDONS;
    }
}
