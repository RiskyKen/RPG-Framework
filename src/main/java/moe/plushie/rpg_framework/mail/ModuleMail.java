package moe.plushie.rpg_framework.mail;

import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.module.ModModule;
import moe.plushie.rpg_framework.mail.common.TableMail;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModuleMail extends ModModule {

    public ModuleMail() {
        super("mail");
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
        RPGFramework.getProxy().getMailSystemManager().reload(false);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TableMail.create();
            }
        });
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent event) {
    }
}
