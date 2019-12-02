package moe.plushie.rpg_framework.core.common.config;

import com.google.gson.annotations.Expose;

public final class ConfigOptions {
    
    // General
    @Expose
    public int heatmapTrackingRate = 0;

    // Currency
    @Expose
    public boolean showPlayerInventoryInWalletGUI = true;

    // Mail
    @Expose
    public boolean showPlayerInventoryInMailGUI = true;

    // Shop
    @Expose
    public boolean showPlayerInventoryInShopGUI = true;

    // Other
    public String lastVersion;
    public boolean hasUpdated;
}
