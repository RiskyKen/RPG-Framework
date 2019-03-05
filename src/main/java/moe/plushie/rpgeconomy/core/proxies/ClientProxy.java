package moe.plushie.rpgeconomy.core.proxies;

import moe.plushie.rpgeconomy.core.client.gui.GuiResourceManager;
import moe.plushie.rpgeconomy.core.client.model.ICustomModel;
import moe.plushie.rpgeconomy.core.client.settings.Keybindings;
import moe.plushie.rpgeconomy.core.common.init.ModBlocks;
import moe.plushie.rpgeconomy.core.common.init.ModItems;
import moe.plushie.rpgeconomy.core.common.lib.LibModInfo;
import moe.plushie.rpgeconomy.core.common.module.IModModule;
import moe.plushie.rpgeconomy.core.common.module.ModModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = LibModInfo.ID, value = { Side.CLIENT })
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        new GuiResourceManager();
        //File file = new File(event.getSuggestedConfigurationFile().getParentFile(), "sql.db");
    }
    
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        Keybindings.registerKeyBindings();
    }
    
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (int i = 0; i < ModBlocks.BLOCK_LIST.size(); i++) {
            Block block = ModBlocks.BLOCK_LIST.get(i);
            if (block instanceof ICustomModel) {
                ((ICustomModel)block).registerModels();
            }
        }
        for (int i = 0; i < ModItems.ITEM_LIST.size(); i++) {
            Item item = ModItems.ITEM_LIST.get(i);
            if (item instanceof ICustomModel) {
                ((ICustomModel)item).registerModels();
            }
        }
    }
    
    @Override
    public void initRenderers() {
        for (IModModule module : ModModule.MOD_MODULES) {
            module.initRenderers();
        }
    }
}
