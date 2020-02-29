package moe.plushie.rpg_framework.core.proxies;

import moe.plushie.rpg_framework.core.client.gui.GuiIconRenderer;
import moe.plushie.rpg_framework.core.client.gui.GuiResourceManager;
import moe.plushie.rpg_framework.core.client.model.ICustomModel;
import moe.plushie.rpg_framework.core.client.settings.Keybindings;
import moe.plushie.rpg_framework.core.common.addons.ModAddonManager;
import moe.plushie.rpg_framework.core.common.init.ModBlocks;
import moe.plushie.rpg_framework.core.common.init.ModItems;
import moe.plushie.rpg_framework.core.common.lib.LibModInfo;
import moe.plushie.rpg_framework.core.common.module.IModModule;
import moe.plushie.rpg_framework.core.common.module.ModModule;
import moe.plushie.rpg_framework.core.common.utils.RenderUtils;
import moe.plushie.rpg_framework.mail.client.RenderBlockMailBox;
import moe.plushie.rpg_framework.mail.common.tileentities.TileEntityMailBox;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        Keybindings.registerKeyBindings();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMailBox.class, new RenderBlockMailBox());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (int i = 0; i < ModBlocks.BLOCK_LIST.size(); i++) {
            Block block = ModBlocks.BLOCK_LIST.get(i);
            if (block instanceof ICustomModel) {
                ((ICustomModel) block).registerModels();
            }
        }
        for (int i = 0; i < ModItems.ITEM_LIST.size(); i++) {
            Item item = ModItems.ITEM_LIST.get(i);
            if (item instanceof ICustomModel) {
                ((ICustomModel) item).registerModels();
            }
        }
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event) {
        RenderUtils.clearItemStackModelCache();
    }

    @Override
    public void initRenderers() {
        for (IModModule module : ModModule.MOD_MODULES) {
            module.initRenderers();
        }
        ModAddonManager.initRenderers();
        new GuiIconRenderer();
    }
}
