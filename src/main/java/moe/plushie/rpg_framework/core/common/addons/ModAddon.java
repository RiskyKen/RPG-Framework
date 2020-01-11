package moe.plushie.rpg_framework.core.common.addons;

import moe.plushie.rpg_framework.core.RPGFramework;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModAddon {

    private final String modId;
    private final String modName;
    private final boolean isModLoaded;

    public ModAddon(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
        this.isModLoaded = setIsModLoaded();
        if (isModLoaded) {
            RPGFramework.getLogger().info(String.format("Loading %s Compatibility Addon", getModName()));
            ModAddonManager.getLoadedAddons().add(this);
        }
    }

    protected boolean setIsModLoaded() {
        return Loader.isModLoaded(modId);
    }

    public void preInit() {
    }

    public void init() {
    }

    public void postInit() {
    }

    @SideOnly(Side.CLIENT)
    public void initRenderers() {
    }

    public String getModId() {
        return this.modId;
    }

    public String getModName() {
        return this.modName;
    }

    public boolean isModLoaded() {
        return isModLoaded;
    }
}
