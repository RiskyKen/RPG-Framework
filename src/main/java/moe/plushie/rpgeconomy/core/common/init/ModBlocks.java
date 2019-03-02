package moe.plushie.rpgeconomy.core.common.init;

import java.util.ArrayList;

import moe.plushie.rpgeconomy.auction.common.blocks.BlockAuction;
import moe.plushie.rpgeconomy.core.common.blocks.ICustomItemBlock;
import moe.plushie.rpgeconomy.mail.common.blocks.BlockMailBox;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModBlocks {
    
    public static final ArrayList<Block> BLOCK_LIST = new ArrayList<Block>();
    
    public static final Block MAIL_BOX = new BlockMailBox();
    public static final Block AUCTION = new BlockAuction();
    
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
}
