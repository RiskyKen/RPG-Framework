package moe.plushie.rpgeconomy.core.common.module;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModModule {
    

    public void preInit(FMLPreInitializationEvent event);
    
    public void init(FMLInitializationEvent event);
    
    @SideOnly(Side.CLIENT)
    public void initRenderers();
    
    public void postInit(FMLPostInitializationEvent event);
}
