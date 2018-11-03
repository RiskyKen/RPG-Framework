package moe.plushie.rpgeconomy.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import moe.plushie.rpgeconomy.common.lib.LibBlockNames;
import moe.plushie.rpgeconomy.common.tileentities.TileEntityMailBox;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class ModBlocks {
    
    public static Block MAIL_BOX;
    
    public ModBlocks() {
        MAIL_BOX = new BlockMailBox();
    }
    
    public void registerTileEntities() {
        registerTileEntity(TileEntityMailBox.class, LibBlockNames.MAIL_BOX);
    }
    
    private void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, "tileEntity." + id);
    }
}
