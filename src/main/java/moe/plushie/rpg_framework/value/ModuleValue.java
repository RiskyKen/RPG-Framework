package moe.plushie.rpg_framework.value;

import java.io.File;

import moe.plushie.rpg_framework.core.common.module.ModModule;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleValue extends ModModule {

    private static ValueManager valueManager;

    public ModuleValue(File modDirectory) {
        super("value");
        valueManager = new ValueManager(modDirectory);
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
        valueManager.reload();
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent event) {
    }

    public static ValueManager getManager() {
        return valueManager;
    }
}
