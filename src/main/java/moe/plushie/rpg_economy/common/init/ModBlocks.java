package moe.plushie.rpg_economy.common.init;

import java.util.ArrayList;

import moe.plushie.rpg_economy.common.blocks.BlockMailBox;
import moe.plushie.rpg_economy.common.blocks.ICustomItemBlock;
import moe.plushie.rpg_economy.common.lib.LibBlockNames;
import moe.plushie.rpg_economy.common.lib.LibModInfo;
import moe.plushie.rpg_economy.common.tileentities.TileEntityMailBox;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModBlocks {
    
    public static final ArrayList<Block> BLOCK_LIST = new ArrayList<Block>();
    
    public static final Block MAIL_BOX = new BlockMailBox();
    
    public ModBlocks() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> reg = event.getRegistry();
        for (int i = 0; i < BLOCK_LIST.size(); i++) {
            reg.register(BLOCK_LIST.get(i));
        }
    }
    
    @SubscribeEvent
    public void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (int i = 0; i < BLOCK_LIST.size(); i++) {
            Block block = BLOCK_LIST.get(i);
            if (block instanceof ICustomItemBlock) {
                ((ICustomItemBlock)block).registerItemBlock(registry);
            }
        }
    }
    
    public void registerTileEntities() {
        registerTileEntity(TileEntityMailBox.class, LibBlockNames.MAIL_BOX);
    }
    
    private void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, new ResourceLocation(LibModInfo.ID, "tileentity." + id));
    }
}
