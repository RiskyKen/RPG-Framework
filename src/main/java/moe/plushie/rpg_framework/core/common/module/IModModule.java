package moe.plushie.rpg_framework.core.common.module;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModModule {

    public String getName();

    public void preInit(FMLPreInitializationEvent event);

    public void init(FMLInitializationEvent event);

    @SideOnly(Side.CLIENT)
    public void initRenderers();

    public void postInit(FMLPostInitializationEvent event);

    public void serverAboutToStart(FMLServerAboutToStartEvent event);

    public void serverStarting(FMLServerStartingEvent event);

    public void serverStopping(FMLServerStoppingEvent event);
}
