package moe.plushie.rpg_framework.market;

import java.io.File;

import moe.plushie.rpg_framework.core.common.module.ModModule;
import moe.plushie.rpg_framework.market.common.MarketManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleMarket extends ModModule {

    private static MarketManager marketManager;

    public ModuleMarket(File modDirectory) {
        super("market");
        marketManager = new MarketManager(modDirectory);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void init(FMLInitializationEvent event) {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void initRenderers() {
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Override
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent event) {
    }
}
